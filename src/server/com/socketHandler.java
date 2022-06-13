package server.com;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.*;
import client.com.Message;

public class socketHandler implements Runnable {  
    private final Socket socket;
    private Logger log=LogManager.getLogger(socketHandler.class.getName());
    private ObjectInputStream objInStream;
    private ObjectOutputStream objOutStream;
    private List<Message> inMsg = new ArrayList<Message>();
    private List<Message> outMsg = new ArrayList<Message>();
    private static ServerHelper sHelper=new ServerHelper();

    public socketHandler(Socket socket) {
         this.socket = socket;
     }
    
    @Override
    @SuppressWarnings("unchecked")
    public void run() {  
        try {
            //start communication
            objOutStream=new ObjectOutputStream(socket.getOutputStream());
            objInStream = new ObjectInputStream(socket.getInputStream());
            
            log.info("\n\n");
            log.info("Server: Start communication.");

            boolean loggedout=false;
            //read msg from client
            while (!loggedout){
                try{
                    //get client request
                    inMsg = (List<Message>) objInStream.readObject();
                } catch(Exception e1){
                    e1.printStackTrace();
                }

                log.info("Server: Received [" + inMsg.size() + "] messages from: " + socket);
                for (Message msg:inMsg){
                    //received logout msg to end communication
                    if (msg.getAction().equals("logout")){
                        log.info("Server: Logout request from client.");
                        ServerCommunicator.concurrentEditClients.remove(this);
                        ServerCommunicator.loginClients.remove(msg.getUsername());
                        loggedout=true;
                        break;
                    }
                    //process other request
                    Message msgR=sHelper.handleMsg(msg);
                    //add login socket
                    if (msgR!=null && msgR.getAction().equals("login") && msgR.getStatus().equals("valid")){
                        ServerCommunicator.loginClients.put(msgR.getUsername(),this);
                    }
                    //sendMsgToClient
                    log.info("Server: Sending response to client.");
                    if(msgR!=null && msgR.getAction().equals("concurrent_edit") && (
                        msgR.getStatus().equals("server_request") || msgR.getStatus().equals("update"))){
                        for(socketHandler client: ServerCommunicator.concurrentEditClients){
                            client.sendMsgToClient(msgR);
                        }
                    }else{
                        if(msgR!=null) {sendMsgToClient(msgR);}
                    }
                }
                clearMsg(); //clear received and processed msg
            }
            log.info("Server: User Logged out.");
        }catch (IOException e) {
            log.error("Server: Socket error: " + socket);
            e.printStackTrace();
        }finally{
            try{
                socket.close();
            }catch(IOException e){
                log.error("Server: Socket close failed!");
                e.printStackTrace();
            }
            log.info("Server: Closed "+ socket);;
        }
    }

    public void clearMsg() throws IOException{
        inMsg.clear();
        outMsg.clear();
        objOutStream.reset();
    }

    public void sendMsgToClient(Message msg) throws IOException {
        if (msg!=null){
            outMsg.clear();
            outMsg.add(msg);
            objOutStream.writeUnshared(outMsg);
            objOutStream.flush();
        }
    }
}

