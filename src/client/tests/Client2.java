package client.tests;

import client.com.*;
import java.io.File;
import org.apache.logging.log4j.*;

public class Client2 {
    private static final Logger LOG = LogManager.getLogger(ClientMain.class.getName());
    public static void main(String[] args){
        CurrentUser user = new CurrentUser("visitor2","asd123");
        ClientCommunicator client = new ClientCommunicator("127.0.0.2");
        LOG.info("Client: Build communication!");

        //Action: login
        if (!client.loginSignup(user.getUsername(), user.getPassword(),"login")){
            LOG.error("Client: login failed");
            return;
        }
        LOG.info("Client: User:"+user.toString());
        user.login();

        //Action:delete file/folder
        if(!client.deleteFileFolder(user.getUsername(), user.getPassword(),user.getUsername()+"/firstfile.txt")){
            LOG.error("Client: deleteFileFolder failed");
        }

        //Action: ViewFolder
        File[] files = client.viewFolderContents(user.getUsername(),user.getPassword(),user.getUsername());
        for(File file : files){
            LOG.info(file.getPath());
        }
        LOG.info("Client: ViewUserFiles Test finished.");

        //Action: logout
        client.logout(user.getUsername());

        client.Ends();

    }
}
