package uwu.misaka.net;

import uwu.misaka.BaseCoin;
import uwu.misaka.ConnectCodes;
import uwu.misaka.SkatCoinServer;
import uwu.misaka.Wallet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

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
        wallet = SkatCoinServer.walletDao.getByLogin(login.substring(5));
        if (wallet == null||!login.startsWith(ConnectCodes.SENDED_LOGIN)) {
            writeLn(ConnectCodes.USER_NOT_EXIST);
        } else {
            if (SkatCoinServer.activeWallets.contains(wallet)) {
                writeLn(ConnectCodes.ALREADY_CONNECTED);
                writeLn(ConnectCodes.CLOSE_CHANNEL_FMS);
                return;
            }
            writeLn(ConnectCodes.REQUEST_PASSWORD);
            String password = br.readLine();
            if (password.substring(5).equals(wallet.password)&&password.startsWith(ConnectCodes.SENDED_PASSWORD)) {
                connection = new Reader();
                connection.start();
                SkatCoinServer.activeWallets.add(wallet);
                writeLn(ConnectCodes.LOGIN_SUCCES);
                System.out.println("[ClientConnector]   "+wallet.login+" вошел в систему");
                return;
            } else {
                writeLn(ConnectCodes.PASSWORD_INVALID);
                writeLn(ConnectCodes.CLOSE_CHANNEL_FMS);
                System.out.println("[ClientConnector]   "+wallet.login+" ввел неверный пароль");
                close();
                return;
            }
        }
        register();
    }

    public void register() {
        try {
            writeLn(ConnectCodes.REGISTRATION_REQUEST);
            writeLn(ConnectCodes.REQUEST_LOGIN);
            String login = br.readLine();
            writeLn(ConnectCodes.REQUEST_PASSWORD);
            String password = br.readLine();
            if (SkatCoinServer.walletDao.getById(login) != null) {
                writeLn(ConnectCodes.REGISTRATION_NAME_USED);
            }
            Wallet wallet = new Wallet(Objects.hash(login) + "", login, password);
            SkatCoinServer.walletDao.save(wallet);
            writeLn(ConnectCodes.REGISTRATION_SUCCES);
            writeLn(ConnectCodes.CLOSE_CHANNEL_FMS);
            System.out.println("[ClientConnector]   "+wallet.login+" зарегистрирован");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeLn(String line) {
        try {
            bw.write(line + "\n");
            bw.flush();
        } catch (Exception e) {
            close();
        }
    }

    public void handleInput(String s) {
        if (!s.startsWith(ConnectCodes.CLIENT_HEADER)) {
            writeLn(ConnectCodes.CLOSE_CHANNEL_FMS);
            close();
            System.out.println("[" + wallet.login + "] НЕ корректный запрос. канал закрыт");
            return;
        }
        if (s.startsWith(ConnectCodes.CLOSE_CHANNEL_FMC)) {
            close();
            System.out.println("[" + wallet.login + "] ЗАКРЫЛ СОЕДИНЕНИЕ");
            return;
        }
        if (s.startsWith(ConnectCodes.REQUEST_BALANCE)) {
            writeLn(ConnectCodes.SEND_BALANCE + wallet.walletCoins.size());
            System.out.println("[" + wallet.login + "] Запросил баланс");
            return;
        }
        if (s.startsWith(ConnectCodes.REQUEST_MINING_PICTURE)) {
            sendMiningPicture();
            System.out.println("[" + wallet.login + "] Запросил изображение");
            return;
        }
        if (s.startsWith(ConnectCodes.FALSE) || s.startsWith(ConnectCodes.TRUE)) {
            parseAnswer(s);
            System.out.println("[" + wallet.login + "] Прислал решение");
            return;
        }
        if (s.startsWith(ConnectCodes.TRANSFER_SKATCOINS)) {
            transferSkatCoins(s);
            System.out.println("[" + wallet.login + "] Запросил перевод");
            return;
        }
    }

    public void sendMiningPicture(){
        File f = new File("trueornya.png");
        if(f.getPath().contains("true")){
            connection.answer=true;
        }
        if(f.getPath().contains("false")){
            connection.answer=false;
        }
        try {
            Image image = ImageIO.read(f);
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage) image,"png",s);
            writeLn(ConnectCodes.SEND_MINING_IMAGE+s);
            connection.isAnswer=true;
        }catch (Exception e){
           e.printStackTrace();
        }
    }

    public void close() {
        try {
            br.close();
            bw.close();
            socket.close();
            SkatCoinServer.walletDao.save(wallet);
            SkatCoinServer.activeWallets.remove(wallet);
            connection.interrupt();
        } catch (Exception ignored) {

        }
    }

    public void transferSkatCoins(String transfer_request) {
        transfer_request = transfer_request.substring(5);
        Wallet target = SkatCoinServer.walletDao.getByLogin(transfer_request.split("#")[0]);
        if (target == null) {
            writeLn(ConnectCodes.TRANSFER_TARGET_ERROR);
            return;
        }
        int transferCoins = Integer.parseInt(transfer_request.split("#")[1]);
        if (transferCoins > wallet.walletCoins.size()) {
            writeLn(ConnectCodes.NOT_ENOUGH_SKATCOINS);
            return;
        }
        ArrayList<String> nya = new ArrayList<>();
        for (int i = 0; i < transferCoins; i++) {
            String s = wallet.walletCoins.get(0);
            nya.add(s);
            wallet.walletCoins.remove(0);
        }
        if (SkatCoinServer.activeWallets.contains(target)) {
            target.walletCoins.addAll(nya);
        }
        nya.forEach(cid -> {
            BaseCoin c = SkatCoinServer.baseCoinDao.getById(cid);
            c.walletId = target.id;
            SkatCoinServer.baseCoinDao.save(c);
        });
        writeLn(ConnectCodes.SEND_BALANCE + wallet.walletCoins.size());
        writeLn(ConnectCodes.SUCCES_SKATCOINS_TRANSFER);
    }

    public void parseAnswer(String code) {
        if (!connection.isAnswer) {
            writeLn(ConnectCodes.SHIZA_SOLVING);
            return;
        }
        if ((code.equals(ConnectCodes.TRUE) && connection.answer) || (code.equals(ConnectCodes.FALSE) && !connection.answer)) {
            BaseCoin coin = new BaseCoin("", wallet.id);
            wallet.walletCoins.add(coin.id);
            SkatCoinServer.baseCoinDao.save(coin);
            writeLn(ConnectCodes.SUCCES_SOLVING);
        }
    }

    public class Reader extends Thread {
        public boolean answer = false;
        public boolean isAnswer = false;

        @Override
        public void run() {
            while (true) {
                try {
                    String input = br.readLine();
                    handleInput(input);
                } catch (Exception e) {
                    System.out.println("[" + wallet.login + "] Прислал ошибку");
                    break;
                }
            }
            close();
        }
    }
}
