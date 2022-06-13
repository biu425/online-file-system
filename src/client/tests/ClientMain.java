package client.tests;

import client.com.*;
//import java.io.File;
import java.util.*;
import org.apache.logging.log4j.*;

public class ClientMain {
    private static final Logger LOG = LogManager.getLogger(ClientMain.class.getName());
    public static void main(String[] args){
        CurrentUser user = new CurrentUser("visitor","qwe123");
        ClientCommunicator client = new ClientCommunicator("127.0.0.5");
        LOG.info("Client: Build communication!");

        //Action:signup
        if (!client.loginSignup(user.getUsername(), user.getPassword(),"signup")){
            LOG.error("Client: sign up failed");
        }
        System.out.println("");

        //Action: login
        if (!client.loginSignup(user.getUsername(), user.getPassword(),"login")){
            LOG.error("Client: login failed");
            return;
        }
        LOG.info("Client: User:"+user.toString());
        user.login();

        /*
        //Action: Change password
        if (!client.changeAccount(user.getUsername(),user.getPassword(),"asd123","password")){
            System.out.println("Client: change password failed");
            return;
        }
        user.setPassword("asd123");
        LOG.info("Client: User:"+user.toString()+"\n");

        //Action: ViewFolder
        File[] files = client.viewFolderContents(user.getUsername(),user.getPassword(),user.getUsername());
        for(File file : files){
            LOG.info(file.getPath());
        }
        LOG.info("Client: ViewUserFiles Test finished.");

        //Action:Create file/folder
        if(!client.createFileFolder(user.getUsername(), user.getPassword(), user.getUsername()+"/test/test.txt")){
            LOG.error("Client: createFileFolder failed");
        }

        //Action:delete file/folder
        if(!client.deleteFileFolder(user.getUsername(), user.getPassword(),user.getUsername()+"/test")){
            LOG.error("Client: deleteFileFolder failed");
        }
        
        //Action:download txt file
        boolean flag=client.downloadFile(user.getUsername(), user.getPassword(), user.getUsername()+"/firstfile.txt");
        if(!flag){
            LOG.error("Client: Download txt failed");
        }
        LOG.info("Client: Download txt file");

        //Action:Upload folder
        String src_path ="C:/Study/java_works/coen275-project/OnlineFile/testupload";
        if(!client.upload(user.getUsername(),user.getPassword(), src_path, user.getUsername())){
            LOG.error("Client: Upload folder failed.");
        }
        LOG.info("Client: Upload folder successful.");

        //Action: share file
        String path ="visitor/testupload/testupload.txt";
        if(!client.shareFile(user.getUsername(), user.getPassword(), path, "visitor2")){
            LOG.error("Client: Share file failed.");
        }
        LOG.info("Client: Share file successfully.");
        */

        //Action:server-multi clients communication
        List<String> users=new ArrayList<String>();
        users.add("visitor2");
        Message message =new Message("concurrent_edit","requesting","visitor","qwe123","test.txt",users);
        client.concurrentEdit(message);

        System.out.println("Enter to end");
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        Scanner in2 = new Scanner(line);
      
        while(in2.hasNextLine()){
            ;
        }
        in.close();
        in2.close();

        //Action: logout
        client.logout(user.getUsername());

        client.Ends();
    }
}
