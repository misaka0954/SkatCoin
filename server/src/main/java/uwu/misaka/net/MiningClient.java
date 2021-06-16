package uwu.misaka.net;

import uwu.misaka.BaseCoin;
import uwu.misaka.ConnectCodes;
import uwu.misaka.SkatCoinServer;
import uwu.misaka.Wallet;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
            parseAnswer(s);
            System.out.println("["+wallet.login+"] Прислал решение");
            return;
        }
        if(s.startsWith(ConnectCodes.TRANSFER_SKATCOINS)){
            transferSkatCoins(s);
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

    public void transferSkatCoins(String transfer_request){
        transfer_request=transfer_request.substring(5);
        Wallet target = SkatCoinServer.walletDao.getById(transfer_request.split("||")[0]);
        if(target==null){
            writeLn(ConnectCodes.TRANSFER_TARGET_ERROR);
            return;
        }
        int transferCoins = Integer.parseInt(transfer_request.split("||")[1]);
        if(transferCoins>wallet.walletCoins.size()){
            writeLn(ConnectCodes.NOT_ENOUGH_SKATCOINS);
            return;
        }
        ArrayList<String> nya = new ArrayList<>();
        for(int i = 0; i<transferCoins;i++){
            String s = wallet.walletCoins.get(0);
            nya.add(s);
            wallet.walletCoins.remove(0);
        }
        if(SkatCoinServer.activeWallets.contains(target)){
            nya.forEach(c->target.walletCoins.add(c));
        }
        nya.forEach(cid->{
            BaseCoin c = SkatCoinServer.baseCoinDao.getById(cid);
            c.walletId= target.id;
            SkatCoinServer.baseCoinDao.save(c);
        });
        writeLn(ConnectCodes.SEND_BALANCE+wallet.walletCoins.size());
    }

    public void parseAnswer(String code){
        if(!connection.isAnswer){
            writeLn(ConnectCodes.SHIZA_SOLVING);
            return;
        }
        if((code.equals(ConnectCodes.TRUE)&& connection.answer)||(code.equals(ConnectCodes.FALSE)&&!connection.answer)){
            BaseCoin coin = new BaseCoin("", wallet.id);
            wallet.walletCoins.add(coin.id);
            SkatCoinServer.baseCoinDao.save(coin);
            writeLn(ConnectCodes.SUCCES_SOLVING);
        }
    }

    public class Reader extends Thread{
        public boolean answer=false;
        public boolean isAnswer=false;

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
