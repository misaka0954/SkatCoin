package uwu.misaka;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Ohayo{
    public static Socket socket;
    
    public static BufferedReader in;
    public static BufferedWriter out;
    
    public static void main(String[] arguments){
        makeMeConnected();
        if(socket==null){
            // TODO: 17.06.2021 show error hentai picture / screen
            return;
        }

    }
    
    public static void makeMeConnected(){
        try{
            socket = new Socket("127.0.0.1",27735);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
