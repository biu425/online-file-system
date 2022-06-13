package server.com;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.*;;

public class ServerCommunicator implements Runnable {
    public static final int MAXTHREAD = 10;
    public static final int port=2022;  
    private ServerSocket serverSocket;  
    private ExecutorService threadpool; 
    public static List<socketHandler> concurrentEditClients=new ArrayList<>();
    public static Map<String, socketHandler> loginClients=new HashMap<>();
    private Logger log=LogManager.getLogger(ServerCommunicator.class.getName());
   
    public ServerCommunicator() {
        try{
            serverSocket = new ServerSocket(port);  
            threadpool = Executors.newFixedThreadPool(MAXTHREAD);
        }  catch(Exception e) {
            log.error("Server: Start servercommunicator failed");
            System.exit(1);
        }
          
    }  
   
    public void run() { // run the service  
        try {  
            for (;;) { 
                Socket socket=serverSocket.accept();
                threadpool.execute(new socketHandler(socket));  
            }  
        } catch (IOException ex) {  
            threadpool.shutdown();  
        }  
    }

}  