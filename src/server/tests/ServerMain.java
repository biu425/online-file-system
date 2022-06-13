package server.tests;

import server.com.*;

public class ServerMain {
    public static void main(String[] args){
        ServerCommunicator server=new ServerCommunicator();
        //server.shutdownAndAwaitTermination();
        server.run();
    }
}
