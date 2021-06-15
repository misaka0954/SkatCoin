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
            while(true){
                System.out.println("[ClientConnector]   Waiting for connection");
                try {
                    new MiningClient(server.accept());
                    System.out.println("[ClientConnector]   Mining Client accepted");
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
