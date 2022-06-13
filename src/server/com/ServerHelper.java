package server.com;

import client.com.Message;
import org.apache.logging.log4j.*;

public class ServerHelper {
    private static ConcurrentEditHandler editHandler = new ConcurrentEditHandler();
    private AllUser allUser=new AllUser();
    private final String FILE_PATH="src/server/allfiles/";
    private Logger log = LogManager.getLogger(ServerHelper.class.getName());

    //constructor
    public ServerHelper(){
        allUser.loadUserData();
    }

    //create a new user
    public boolean newUser(Message message) {
        if(allUser.addUser(message.getUsername(),message.getPassword())){
            message.setStatus("valid");
            return true;
        } else{
            message.setStatus("invalid");
            return false;
        }
    }

    //check if user is valid
    public Message validateUser(Message message) {
        if(allUser.validUser(message.getUsername(),message.getPassword())!=null){
            message.setStatus("valid");
        }
        return message;
    }

    //handle user password change
    public Message changePassword(Message message) {
        log.info("Server: Received changePassword request.");
        if (allUser.modUserPassword(message.getUsername(),message.getPassword(),message.getText())){
            message.setStatus("success");
        }
        else{
            message.setStatus("fall");
        }
        return message;
    }

    //handle username change
    public Message changeUsername(Message message) {
        log.info("Server: Received changeUsername request.");
        if (allUser.modUserName(message.getUsername(),message.getPassword(),message.getText())){
            message.setStatus("success");
        }
        else{
            message.setStatus("fall");
        }
        return message;
    }

    //handel login message from client
    public Message handleLoginMsg(Message message) {
        log.info("Server: Received login message.");
        Message msgR=validateUser(message);
        return msgR;
    }

    //handle sign up message from client
    public Message handleSignupMsg(Message message) {
        log.info("Server: Received signup message.");
        if (!newUser(message)){
            log.warn("Server: Warning: This username is already in use!");
            return message;
        }
        String path=FILE_PATH+message.getUsername();
        FileHandler newFile=new FileHandler(path+"/firstfile.txt");
        newFile.writeTxtFile("This is your first file!");
        return message;
    }

    //handle response : create new file/folder
    public Message handleCreateMsg(Message message){
        log.info("Server: Received message for creating new file/folder.");
        if(allUser.validUser(message.getUsername(),message.getPassword())==null){
            log.info("Server: Received msg from invalid user!");
            message.setStatus("fail");
            return message;
        }
        FileHandler newFile=new FileHandler(FILE_PATH+message.getText());
        log.info("Server: Create"+newFile.getFile().getAbsolutePath()+" successfully.");
        message.setStatus("success");
        return message;
    }

    //handle response : delete new file/folder
    public Message handleDeleteMsg(Message message){
        log.info("Server: Received message for deleting file/folder.");

        if(message.getText()==null){
            message.setStatus("fail");
            return message;
        }

        FileHandler newFile=new FileHandler();
        boolean flag=false;
        if(message.getText().endsWith(".txt")){
            flag=newFile.deleteFile(FILE_PATH + message.getText());
        }else{
            flag=newFile.deleteDirectory(FILE_PATH+message.getText());
        }
        
        if(flag){
            log.info("Server: Delete successfully.");
            message.setStatus("success");
        }else{
            log.error("Server: Delete file/folder failed.");
            message.setStatus("fail");
        }
        return message;
    }

    //handle view user files in the specified directory
    public Message handleViewFolder(Message message) {
        log.info("Server: Received message for viewing folder contents.");
        
        FileHandler newFile=new FileHandler(FILE_PATH+message.getText());
        message.setFiles(newFile.getAllFiles()); //this folder maybe empty, so cannot use null to judge fail or success
        message.setStatus("response");
        message.setFileTimes(newFile.getAllFileTimes());
        return message;
    }

    //handle download request for specified txt file or read file
    public Message handleDownloadMsg(Message message){
        log.info("Server: Received download file message.");
        
        FileHandler newFile=new FileHandler(FILE_PATH+message.getText());
        byte[] txt=newFile.fileToByteArray();
        if(txt==null){
            message.setStatus("fail");
            log.error("Server: Download failed.");
        }else{
            message.setBytes(txt);
            message.setStatus("success");
            log.info("Server: Download successfully.");
        }
        return message;
    }

    //handle upload request for specified txt file
    public Message handleUploadFileMsg(Message message){
        log.info("Server: Received upload file message.");
        
        FileHandler newFile=new FileHandler(FILE_PATH+message.getText());
        if (newFile.byteArrayToFile(message.getBytes())){
            message.setStatus("success");
            message.setBytes(null);
            log.info("Server: Upload file successfully.");
        }else{
            log.error("Server: Upload file failed.");
            message.setStatus("fail");
            message.setBytes(null);
        }
        return message;
    }

    //handle share request
    public Message handleShareMsg(Message message){
        log.info("Server: Received share file request.");

        if(allUser.getUser(message.getText2())==null){
            message.setStatus("fail"); //shared user not exists
            return message;
        }
        
        //file contents
        FileHandler src=new FileHandler(FILE_PATH+message.getText());
        byte[] filebytes=src.fileToByteArray();
        //share msg info
        String tempstr="#Shared file from user: "+message.getUsername()+"\n\n";
        byte[] tempbyte=tempstr.getBytes();
        byte[] finalbytes=editHandler.concatWithArrayCopy(tempbyte, filebytes);

        String filename=src.getFile().getName();
        FileHandler dst=new FileHandler(FILE_PATH+message.getText2()+"/shared_files/"+filename);
        if(dst.byteArrayToFile(finalbytes)){
            message.setStatus("success");
            log.info("Server: Share file "+message.getText()+" to "+message.getText2()+" successfully");
        }else{
            message.setStatus("fail");
            log.info("Server: Share file "+message.getText()+" to "+message.getText2()+" failed");
        }
        return message;
    }

    //handle concurrent_edit
    public Message handleConcurrentEditMsg(Message message) {
        log.info("Server: Received concurrent editing request.");
        return editHandler.handleMsg(message);
    }

    //handle msg from client
    public Message handleMsg(Message message){
        String type = message.getAction();
        switch(type){
            case "login": return handleLoginMsg(message);
            case "signup": return handleSignupMsg(message);
            case "passwordChange": return changePassword(message);
            case "usernameChange": return changeUsername(message);
            case "viewFolderContents": return handleViewFolder(message);
            case "create": return handleCreateMsg(message);
            case "delete": return handleDeleteMsg(message);
            case "download": return handleDownloadMsg(message);
            case "uploadFile": return handleUploadFileMsg(message);
            case "share": return handleShareMsg(message);
            case "concurrent_edit": return handleConcurrentEditMsg(message);
            default: 
                log.warn("Received bad message!");
                return null;
        }
    }
}
