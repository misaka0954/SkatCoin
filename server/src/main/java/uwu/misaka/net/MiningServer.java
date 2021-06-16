package uwu.misaka.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class MiningServer {
    ServerSocket server;
    public static ArrayList<MiningClient> serversConnector;
    public MiningServer() {
        try {
            server = new ServerSocket(27735);
            serversConnector = new ArrayList<>();
            new Connector().start();
        } catch (Exception e) {
            System.out.println("[E]SERVER STARTING ERROR");
            System.exit(2);
        }
    }
    public class Connector extends Thread{
        @Override
        public void run() {
            System.out.println("[ClientConnector]   ОЖИДАНИЕ СОЕДИНЕНИЙ");

            while(true){
                try {
                    new MiningClient(server.accept());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
