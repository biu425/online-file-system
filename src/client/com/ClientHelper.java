package client.com;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import org.apache.logging.log4j.*;
import server.com.FileHandler;

public class ClientHelper {
    private static final String FILE_PATH="src/client/Users/";
    private Logger log = LogManager.getLogger(ClientHelper.class.getName());

    //generate login message and send to server
    public Message generateLogin(String username, String password){
        log.info("Client: Attempt to login");
        Message message = new Message("login","requesting",username,password);
        return message;
    }

    //generate logout message and send to server
    public Message generateLogout(String username){
        log.info("Client: Attempt to logout");
        Message message = new Message("logout","requesting",username);
        return message;
    }

    
    //generate sign up message and send to server
    public Message generateSignup(String username, String password){
        log.info("Client: Attempt to sign up");
        Message message = new Message("signup","requesting",username,password);
        return message;
    }

    //handle server response(from client login request)
    public boolean handleLoginSignup(Message msg){
        if(msg.getStatus().equals("valid")){
            return true;
        }
        return false;
    }

    //generate message to request changes to user password
    public Message generatePasswordChange(String username, String password,String new_password){
        log.info("Client: Attempt to change password");
        Message message = new Message("passwordChange","requesting",username,password,new_password);
        return message;
    }

    //generate message to request changes to username
    public Message generateUsernameChange(String username, String password,String new_username){
        log.info("Client: Attempt to change username");
        Message message = new Message("usernameChange","requesting",username,password,new_username);
        return message;
    }

    //handle the account settings change
    public boolean handleAccountChange(Message msg){
        if(msg.getStatus().equals("success")){
            return true;
        }
        return false;
    }

    //generate message to view user files in the specified directory
    public Message generateViewFolderContents(String username, String password,String path){
        log.info("Client: Attempt to request view user files");
        Message message = new Message("viewFolderContents","requesting",username,password,path);
        return message;
    }

    //handle view user files
    public File[] handleViewFolderContents(Message msg){
        String path=FILE_PATH+msg.getText();
        FileHandler folder =new FileHandler(path);
        log.info("Client: View Folder "+ folder.getFile().getName()+" Contents");
        List<File> files = new ArrayList<File>();
        if (msg.getFiles()==null){
            return null;
        }
        for(String file_name: msg.getFiles()){
            files.add(new FileHandler(path+"/"+file_name).getFile());
        }
        return files.toArray(new File[files.size()]);
    }

    //generate message to view txt file
    public Message generateDownloadFile(String username, String password,String path){
        log.info("Client: Attempt to download specified txtx file");
        Message message = new Message("download","requesting",username,password,path);
        return message;
    }

    //handle download file
    public File handleDownloadFile(Message msg){
        if(msg.getStatus().equals("success")){
            FileHandler newFile= new FileHandler(FILE_PATH+msg.getText());
            newFile.byteArrayToFile(msg.getBytes());
            return newFile.getFile();
        }else{
            return null;
        }
    }

    //generate message to create file/folder
    public Message generateCreate(String username, String password, String path){
        log.info("Client: Attempt to request create file/folder");
        Message message = new Message("create","requesting",username,password,path);
        return message;
    }

    //handle create response
    public boolean handleCreateMsg(Message msg){
        if(msg.getStatus().equals("success")){
            FileHandler newFile=new FileHandler(FILE_PATH+msg.getText());
            log.info("Client: Create "+newFile.getFile().getAbsolutePath()+" successfully.");
            return true;
        } else {
            log.error("Client: Create "+msg.getText()+" failed");
            return false;
        }
    }

    //generate message to delete file/folder
    public Message generateDelete(String username, String password, String path){
        log.info("Client: Attempt to request delete file/folder");
        Message message = new Message("delete","requesting",username,password,path);
        return message;
    }

    //handle delete response
    public boolean handleDeleteMsg(Message msg){
        if(msg.getStatus().equals("success")){
            FileHandler newFile=new FileHandler();
            if(msg.getText().endsWith(".txt")){
                newFile.deleteFile(FILE_PATH+msg.getText());
            }else{
                newFile.deleteDirectory(FILE_PATH+msg.getText());
            }
            log.info("Client: Delete "+newFile.getFile().getAbsolutePath()+" successfully.");
            return true;
        } else {
            log.error("Client: Delete "+msg.getText()+" failed.");
            return false;
        }
    }

    //generate message to upload file
    public Message generateUploadFile(String username, String password,String src_path,String dst_path){
        log.info("Client: Attempt to upload txt file");
        FileHandler tempFile=new FileHandler(src_path);
        String filename=tempFile.getFile().getName();
        Message msg=new Message("uploadFile","requesting",username,password,dst_path+"/"+filename,tempFile.fileToByteArray());
        return msg;
    }

    //handle upload file
    public boolean handleUploadFile(Message msg){
        if(msg.getStatus().equals("success")){
            log.info("Client: Upload "+msg.getText()+" successful");
            FileHandler create_file = new FileHandler(FILE_PATH+msg.getText());
            return create_file.getFile().exists();
        } else {
            log.error("Client: Upload "+msg.getText()+" failed.");
            return false;
        }
    }

    //generate message to share file
    public Message generateShare(String username, String password,String path,String share_name){
        log.info("Client: Attempting to share file "+ path+" to "+share_name);
        Message message = new Message("share","requesting",username,password,path,share_name);
        return message;
    }
    
    //handle share file
    public boolean handleShareFile(Message msg){
        if(msg.getStatus().equals("success")){
            log.info("Client: Share file "+msg.getText()+" to "+msg.getText2()+" successfully.");
            return true;
        } else {
            log.error("Client: Share file "+msg.getText()+" to "+msg.getText2()+" failed.");
            return false;
        }
    }

}
