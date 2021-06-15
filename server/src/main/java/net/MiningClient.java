package net;

import java.io.*;
import java.net.Socket;

public class MiningClient {
    public String login = "0";
    public Socket socket;
    public BufferedReader br;
    public BufferedWriter bw;

    public MiningClient(Socket s) throws IOException {
        socket = s;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        //writeLn(ConnectCodes.REQUEST_LOGIN);
        login = br.readLine();
        //поиск логина в базе
        //если не найден
        //writeLn(ConnectCodes.USER_NOT_EXIST);
        //writeln(ConnectCodes.REQUEST_PASSWORD);
        //запрос пароля из базы
    }

    public boolean writeLn(String line){
        try {
            bw.write(line + "\n");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void handleInput(String s){
        //if(!s.startsWith(ConnectCodes.CLIENT_HEADER)){
            //writeLn(ConnectCodes.CLOSE_CHANNEL_FMS);
            close();
        //    return;}
    }

    public void close(){
        try {
            br.close();
            bw.close();
            socket.close();
        }catch (Exception ignored){

        }
    }

    public class Reader extends Thread{
        @Override
        public void run() {
            while(true){
                try {
                    String input = br.readLine();
                    //обработка ввода с сокета
                }catch (Exception e){
                    break;
                }
               close();
            }
        }
    }
}
