package uwu.misaka.net;

import uwu.misaka.ConnectCodes;
import uwu.misaka.SkatCoinServer;
import uwu.misaka.Wallet;

import java.io.*;
import java.net.Socket;

public class MiningClient {
    public Wallet wallet;
    public Socket socket;
    public BufferedReader br;
    public BufferedWriter bw;
    public Reader connection;

    public MiningClient(Socket s) throws IOException {
        socket = s;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writeLn(ConnectCodes.REQUEST_LOGIN);
        String login = br.readLine();
        System.out.println(login);
        wallet = SkatCoinServer.walletDao.getById(login.substring(5));
        if(wallet==null){
        writeLn(ConnectCodes.USER_NOT_EXIST);}else{
            if(SkatCoinServer.activeWallets.contains(wallet)){
                writeLn(ConnectCodes.ALREADY_CONNECTED);
                writeLn(ConnectCodes.CLOSE_CHANNEL_FMS);
                return;
            }
        writeLn(ConnectCodes.REQUEST_PASSWORD);
        if(br.readLine().substring(5).equals(wallet.password)){
            connection = new Reader();
            connection.start();
            SkatCoinServer.activeWallets.add(wallet);
        }else{
            writeLn(ConnectCodes.CLOSE_CHANNEL_FMS);
            close();
            return;
        }
        }
    }

    public boolean writeLn(String line){
        try {
            bw.write(line + "\n");
            bw.flush();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void handleInput(String s){
        if(!s.startsWith(ConnectCodes.CLIENT_HEADER)){
            writeLn(ConnectCodes.CLOSE_CHANNEL_FMS);
            close();
            System.out.println("["+wallet.login+"] НЕ корректный запрос. канал закрыт");
            return;
        }
        if(s.startsWith(ConnectCodes.CLOSE_CHANNEL_FMC)){
            close();
            System.out.println("["+wallet.login+"] ЗАКРЫЛ СОЕДИНЕНИЕ");
            return;
        }
        if(s.startsWith(ConnectCodes.REQUEST_BALANCE)){
            writeLn(ConnectCodes.SEND_BALANCE+wallet.walletCoins.size());
            System.out.println("["+wallet.login+"] Запросил баланс");
            return;
        }
        if(s.startsWith(ConnectCodes.REQUEST_MINING_PICTURE)){
            //TODO отправка майнинговой картинки
            System.out.println("["+wallet.login+"] Запросил изображение");
            return;
        }
        if(s.startsWith(ConnectCodes.FALSE)||s.startsWith(ConnectCodes.TRUE)){
            //TODO обработка входящего майнингово результата
            System.out.println("["+wallet.login+"] Прислал решение");
            return;
        }
        if(s.startsWith(ConnectCodes.TRANSFER_SKATCOINS)){
            //TODO обработка перевода
            System.out.println("["+wallet.login+"] Запросил перевод");
            return;
        }
    }

    public void close(){
        try {
            br.close();
            bw.close();
            socket.close();
            connection.interrupt();
            SkatCoinServer.walletDao.save(wallet);
            SkatCoinServer.activeWallets.remove(wallet);
        }catch (Exception ignored){

        }
    }

    public class Reader extends Thread{
        @Override
        public void run() {
            while(true){
                try {
                    String input = br.readLine();
                    handleInput(input);
                }catch (Exception e){
                    break;
                }
               close();
            }
        }
    }
}
