package client.com;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.*;
import java.util.concurrent.LinkedBlockingQueue;

import server.com.ConcurrentEditHandler;

@SuppressWarnings("unchecked")
public class ClientCommunicator {
    private final int port=2022;
    private Socket socket;
    private ClientHelper cHelper;
    private ObjectInputStream objInStream;
    private ObjectOutputStream objOutStream;
    private List<Message> outMsg = new ArrayList<Message>();
    private Logger log = LogManager.getLogger(ClientCommunicator.class.getName());
    private LinkedBlockingQueue<Message> inputMsgQue = new LinkedBlockingQueue<Message>();
    public static ConcurrentEditHandler editHandler=new ConcurrentEditHandler();
    public List<String> fileTime;

    private class ListeningThread extends Thread{
        private ObjectInputStream objInStream;
        private LinkedBlockingQueue<Message> inputMsgQue;
        private ListeningThread(ObjectInputStream objInStream, LinkedBlockingQueue<Message> inputMsgQue){
            this.objInStream = objInStream;
            this.inputMsgQue = inputMsgQue;
        }
        
        @Override
        public void run(){
            log.info("\n");
            log.info("Client: Listening thread started.");
            try {
                for(;;){
                    //get push msg, put other msg into queue
                    List<Message> inMsg = (List<Message>) objInStream.readObject();
                    for(Message msg : inMsg){
                        log.info("Client: Got server msg in listening thread.");
                        if(msg.getAction().equals("concurrent_edit")){
                            System.out.println(msg);
                            concurrentEdit(msg);
                        }else{
                            inputMsgQue.add(msg);
                        }
                    }
                }
            } catch (Exception e) {
                log.info("\n");
                log.error("Client: Communicator listening thread ended!");
                e.printStackTrace();
                // System.exit(1);
            }
        }
    }

    //constructor
    public ClientCommunicator(String ip){
        cHelper = new ClientHelper();
        try {
            socket =new Socket(ip,port);

            //set communication
            objOutStream=new ObjectOutputStream(socket.getOutputStream());
            objInStream = new ObjectInputStream(socket.getInputStream());
            
            log.info("\n");
            log.info("Client: Connecting, socket: "+ip+'.'+"2022");

            // Run the listening thread
            Thread listeningThread = new ListeningThread(objInStream, inputMsgQue);
            listeningThread.start();
        }catch(IOException e){
            log.info("\n");
            log.error("Client: Communicator constructor failed!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /*
     * login, type:"login" 
     * sign up, type:"signup"
     */
    public boolean loginSignup(String username, String password, String type){
        Message msg=null;
        if(type.equals("login")){
            msg=cHelper.generateLogin(username, password);
        }else{
            msg=cHelper.generateSignup(username, password);
        }

        try{
            sendMsgToServer(msg);
            log.info("Client: sending "+msg.getAction()+" request.");
            Message serverReply = inputMsgQue.take();
            log.info("Client: "+msg.getAction()+": Received response messages from: " + socket);
            if(cHelper.handleLoginSignup(serverReply)){
                log.info("Client: User "+msg.getAction()+" succeed.");
                return true;
            } 
        } catch(Exception e1){
            e1.printStackTrace();
        }
        log.error("Client: User "+msg.getAction()+" failed.");
        return false;
    }

    //logout
    public void logout(String username){
        Message msg=cHelper.generateLogout(username);
        log.info("Client: USER LOGOUT!");
        try{
            sendMsgToServer(msg);
            inputMsgQue.clear();
        } catch(Exception e){
            log.info("Client: Logging out...");
        }

    }

    /*
     * Change account settings
     * changes: new password/new username
     * type:"password"/"username"
     */
    public boolean changeAccount(String username, String password, String changes,String type){
        Message msg=null;
        if(type.equals("username")){
            msg=cHelper.generateUsernameChange(username, password, changes);
        }else{
            msg=cHelper.generatePasswordChange(username, password, changes);
        }

        try{
            log.info("Client: Sending request to change account settings.");
            sendMsgToServer(msg);
            log.info("Client: sending "+msg.getAction()+" request.");
            Message serverReply = inputMsgQue.take();
            log.info("Client: "+msg.getAction()+": Received response messages from: " + socket);
            if(cHelper.handleAccountChange(serverReply)){
                log.info("Client: User "+msg.getAction()+" succeed.");
                return true;
            } 
        }catch(Exception e){
            e.printStackTrace();
        }
        log.error("Client: changeAccount: Fail.");
        return false;
    }

    /*
     * path: the path to the specified folder
     * return the file list of this folder
     */
    public File[] viewFolderContents(String username, String password, String path){
        Message msg=cHelper.generateViewFolderContents(username, password, path);
        try{
            log.info("Client: Sending request to view user files.");
            sendMsgToServer(msg);
            log.info("Client: sending "+msg.getAction()+" request.");
            Message serverReply = inputMsgQue.take();
            fileTime=serverReply.getFileTimes();
            log.info("Client: "+msg.getAction()+": Received response messages from: " + socket);
            File[] fileList=cHelper.handleViewFolderContents(serverReply);
            if (fileList!=null){
                for(File file:fileList){
                    if(file.isFile()){
                        downloadFile(username, password,path+"/"+file.getName());
                    }
                }
                log.info("Client: viewFolderContents : Succeed.");
                return fileList;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        log.error("Client: No response about view user files.");
        return null;
    }

    /*
     * create file or folder under the user's directory'
     * examples: creating file or folder for visitor
     * create file: visitor/test.txt
     * create folder: visitor/test
     */
    public boolean createFileFolder(String username, String password, String path){
        Message msg=cHelper.generateCreate(username, password, path);
        try{
            log.info("Client: Sending request to create file/folder.");
            sendMsgToServer(msg);
            log.info("Client: sending "+msg.getAction()+" request.");
            Message serverReply = inputMsgQue.take();
            log.info("Client: "+msg.getAction()+": Received response messages from: " + socket);
            if(cHelper.handleCreateMsg(serverReply)){
                log.info("Client: User "+msg.getAction()+" succeed.");
                return true;
            } 
        }catch(Exception e){
            e.printStackTrace();
        }
        log.error("Client: Create file/folder failed.");
        return false;
    }

    /*
     * delete file or folder under the user's directory'
     * examples: deleting file or folder for visitor
     * delete file: visitor/test.txt
     * delete folder: visitor/test
     */
    public boolean deleteFileFolder(String username, String password, String path){
        Message msg=cHelper.generateDelete(username, password, path);
        try{
            log.info("Client: Sending request to delete file/folder.");
            sendMsgToServer(msg);
            log.info("Client: sending "+msg.getAction()+" request.");
            Message serverReply = inputMsgQue.take();
            log.info("Client: "+msg.getAction()+": Received response messages from: " + socket);
            if(cHelper.handleDeleteMsg(serverReply)){
                log.info("Client: User "+msg.getAction()+" succeed.");
                return true;
            } 
        }catch(Exception e){
            e.printStackTrace();
        }
        log.error("Client: Delete file/folder failed.");
        return false;
    }

    /*
     * Download a specified file // View the specified file
     * path of the file: visitor/test.txt (visitor is the user name)
     */
    public boolean downloadFile(String username, String password, String path){
        Message msg=cHelper.generateDownloadFile(username, password, path);
        try{
            log.info("Client: Sending request to download txt file.");
            sendMsgToServer(msg);
            log.info("Client: sending "+msg.getAction()+" request.");
            Message serverReply = inputMsgQue.take();
            log.info("Client: "+msg.getAction()+": Received response messages from: " + socket);
            if(cHelper.handleDownloadFile(serverReply) != null){
                log.info("Client: User "+msg.getAction()+" succeed.");
                return true;
            } 
        }catch(Exception e){
            e.printStackTrace();
        }
        log.error("Client: Download txt file failed.");
        return false;
    }

    /*
     * upload file/folder
     * dst_path : username/*
     */
    public boolean upload(String username, String password, String src_path,String dst_path){
        if (src_path.endsWith(".txt")){
            return uploadFile(username, password, src_path, dst_path);
        }else{
            return uploadFolder(username, password, src_path, dst_path);
        }
    }

    public boolean uploadFile(String username, String password, String src_path,String dst_path){
        Message msg=cHelper.generateUploadFile(username, password, src_path, dst_path);
        try{
            log.info("Client: Sending request to upload txt file.");
            sendMsgToServer(msg);
            log.info("Client: sending "+msg.getAction()+" request.");
            Message serverReply = inputMsgQue.take();
            log.info("Client: "+msg.getAction()+": Received response messages from: " + socket);
            if(cHelper.handleUploadFile(serverReply)){
                log.info("Client: User "+msg.getAction()+" succeed.");
                return true;
            } 
        }catch(Exception e){
            e.printStackTrace();
        }
        log.error("Client: Upload txt file failed.");
        return false;
    }

    public boolean uploadFolder(String username, String password, String src_path,String dst_path){
        File folder=new File(src_path);
        String folder_name=folder.getName();
        if(!createFileFolder(username,password,dst_path+"/"+folder_name)){
            log.error("Client: Error creating folder " + folder_name);
            return false;
        }
        boolean flag=true;
        File[] children=folder.listFiles();
        for(File child : children){
            if(child.isFile()){
                flag=uploadFile(username,password,src_path+"/"+child.getName(),dst_path+"/"+folder.getName());
                if(!flag){break;}
            }else{
                flag=uploadFolder(username,password,src_path+"/"+child.getName(),dst_path+"/"+folder.getName());
                if(!flag){break;}
            }
        }
        if(!flag){
            log.error("Client: Upload folder failed.");
            return false;
        }
        return true;
    }

    /*
     * Recently can only share a file to one user at a time
     * share path should be: username/*, cannot be absolute path
     */
    public boolean shareFile(String username,String password,String path,String share_name){
        if(!path.endsWith(".txt")){
            log.error("Can only share a file not folder.");
            return false;
        }
        Message msg=cHelper.generateShare(username, password, path, share_name);
        try{
            log.info("Client: Sending request to share txt file.");
            sendMsgToServer(msg);
            log.info("Client: sending "+msg.getAction()+" request.");
            Message serverReply = inputMsgQue.take();
            log.info("Client: "+msg.getAction()+": Received response messages from: " + socket);
            if(cHelper.handleShareFile(serverReply)){
                log.info("Client: User "+msg.getAction()+" succeed.");
                return true;
            } 
        }catch(Exception e){
            e.printStackTrace();
        }
        log.error("Client: Share txt file failed.");
        return false;
    }

    /*
     *client firstly requests to concurrent,the message should contain String username,String path, List[String] share_name
     * action: concurrent_edit
     * type: requesting,server
     */
    public void concurrentEdit(Message msg){
        if (msg.getStatus().equals("requesting")){
            log.info("Client: Requesting for concurrent editing file "+ msg.getText()+" with "+msg.getFiles().toString());
            sendMsgToServer(msg);
        } else {
            editHandler.handleMsg(msg);
        }
    }

    public void sendMsgToServer(Message msg) {
        try{
            if (msg!=null){
                outMsg.clear();
                outMsg.add(msg);
                objOutStream.writeUnshared(outMsg);
                objOutStream.flush();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        
    }

    public void Ends(){
        try{
            socket.close();
            log.info("Client: Close socket.");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<String> getUserList(){
        return editHandler.editUserList;
    }

    public ConcurrentEditHandler getEditHandler(){
        return editHandler;
    }
}
