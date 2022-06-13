package server.com;

import client.com.*;
import framework.view.*;
import java.io.File;
import java.util.*;
import org.apache.logging.log4j.*;

public class ConcurrentEditHandler {
    public Logger log = LogManager.getLogger(ConcurrentEditHandler.class.getName());
    private static final String SERVER_ROOTPATH ="src/server/allfiles";
    private static final String SERVER_EDIT_ROOTPATH="src/server/allfiles/conrrentEdit/";
    public String CLIENT_EDIT_PATH;
    private String oldPath;
    private String filename;
    public String owner;
    private  FileHandler originalFile; //server
    public FileHandler editFile; //server or client
    private invitationPage confirmPage;
    public List<String> editUserList=new ArrayList<String>();

    public ConcurrentEditHandler(){}

    public Message handleMsg(Message msg){
        String type=msg.getStatus();

        switch(type) {
            case "requesting": return clientRequest(msg);
            case "server_request": return serverRequest(msg);
            case "agree": return editAgree(msg);
            case "reject": return editReject(msg);
            case "server_response": return serverResponse(msg);
            case "pushChange": return pushChange(msg);
            case "update": return editUpdate(msg);
            case "quit": return editQuit(msg);
            case "final_update": return editFinalUpdate(msg);
            default: 
                log.warn("Received bad message!");
                return null;
        }
    }

    //server: recieved message from client for concurrent editing
    public Message clientRequest(Message message) {
        log.info("Server: requesting: recieved message from client for concurrent editing");
        ServerCommunicator.concurrentEditClients.add(ServerCommunicator.loginClients.get(message.getUsername()));
        oldPath=message.getText();
        log.info("Server: the original file path: "+oldPath);
        filename=(new File(SERVER_ROOTPATH+oldPath)).getName();
        log.info("Server: filename: "+filename);
        owner=message.getUsername();
        log.info("Server: file owner: "+owner);
        editUserList.add(message.getUsername());
        for(String user:message.getFiles()){
            if(ServerCommunicator.loginClients.get(user)!=null){
                editUserList.add(user);
                ServerCommunicator.concurrentEditClients.add(ServerCommunicator.loginClients.get(user));
            }
        }
        log.info("Server: Concurrent editUserList: "+editUserList.toString());
        message.setStatus("server_request");message.setPassword(null);
        message.setText2(message.getUsername()+" invites you to edit file "+message.getText());
        message.setFiles(editUserList);
        return message;
    }

    //client: recieved message from server for concurrent editing
    public Message serverRequest(Message message) {
        log.info("Client: serverRequest: recieved message from server for concurrent editing.");
        log.info("Client: msg: "+message.getText2());
        confirmPage=new invitationPage(0,message,null);
        confirmPage.frame.setVisible(true);
        return null;
    }

    //server: recieved message from one client for agreeing to concurrent editing
    public Message editAgree(Message message) {
        log.info("Server: editAgree: "+message.getUsername()+" accept the invitation.");
        message.setStatus("server_response");
    
        //copy file content to the edit file
        originalFile=new FileHandler(SERVER_ROOTPATH+oldPath);
        //edit file path
        String newPath=SERVER_EDIT_ROOTPATH+filename;

        if(new File(newPath).exists()){
            //if edit file already exists,send content from edit file
            message.setBytes(editFile.fileToByteArray());
        } else{
            //if edit file not exists, copy content to edit file
            log.info("Server: Create the temporary file.");
            byte[] data=originalFile.fileToByteArray();
            editFile=new FileHandler(newPath);
            editFile.byteArrayToFile(data);
            message.setBytes(data);
        }
        message.setFiles(editUserList);
        message.setPassword(filename);
        return message;
    }

    //client: recieved message from server for get file bytes
    public Message serverResponse(Message message) {
        log.info("Client: serverResponse: recieved file bytes from server.");
        editUserList=message.getFiles();
        //create a local temporary file
        filename=message.getPassword();
        owner=message.getText2();
        CLIENT_EDIT_PATH="src/client/Users/"+message.getUsername()+"/concurrentEdit/"+filename;
        log.info("Client: create temporary file "+CLIENT_EDIT_PATH);
        editFile=new FileHandler(CLIENT_EDIT_PATH);
        editFile.byteArrayToFile(message.getBytes());
        message.setBytes(null);
        //close the confirmPage
        confirmPage.frame.dispose();
        log.info("Client: Close invite confirm Page");
        //open file edit page
        log.info("Client: Open file edit page");
        loginPage.fileMngPage.openEditor(CLIENT_EDIT_PATH);
        docuEditPage.concurrentEditing=true;
        return null;
    }

    //server: recieved message from one client for rejecting to concurrent editing
    public Message editReject(Message message) {
        log.info("Server: "+message.getUsername()+" rejects to edit file");
        ServerCommunicator.concurrentEditClients.remove(ServerCommunicator.loginClients.get(message.getUsername()));
        editUserList.remove(message.getUsername());
        log.info("Server: New Concurrent editUserList: "+editUserList.toString());
        return null;
    }

    //helper function for push change command
    public byte[] concatWithArrayCopy(byte[] array1, byte[] array2) {
        byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    //server: recieved message from client for handling edit changes
    public Message pushChange(Message message){
        log.info("Server: Received "+message.getUsername()+" push change request.");
        log.info("Server: server is editing in server temp file");

        byte[] bytes=editFile.fileToByteArray();
        int size = bytes.length;
        EditInfo editInfo = message.getEditInfo();
        message.setStatus("update");
        if(editInfo.mode == 0){ //delete
            log.info("Server: mode: delete");
            if(editInfo.startOffset < 0 || editInfo.endOffset < editInfo.startOffset || size < editInfo.endOffset){
                //Fail, incorrect params
                log.error("Server: incorrect parameters");
                message.setStatus("update_fail");
            }else{
                bytes = concatWithArrayCopy(Arrays.copyOfRange(bytes, 0, editInfo.startOffset), Arrays.copyOfRange(bytes, editInfo.endOffset, size));
            }
        }else if(editInfo.mode == 1){ //insert 
            log.info("Server: mode: insert");
            if(editInfo.startOffset < 0 || size < editInfo.startOffset){
                //Fail, incorrect params
                log.error("Server: incorrect parameters");
                message.setStatus("update_fail");
            }else{
                byte[] temp = concatWithArrayCopy(Arrays.copyOfRange(bytes, 0, editInfo.startOffset), editInfo.text.getBytes());
                bytes = concatWithArrayCopy(temp, Arrays.copyOfRange(bytes, editInfo.startOffset, size));
            }
        }else{
            // Unsupported mode
            log.error("Server: incorrect mode");
            message.setStatus("update_fail");
        }
        //update bytes
        editFile.byteArrayToFile(bytes);
        message.setBytes(bytes);
        message.setFiles(editUserList);
        log.info("Server: push change successfully");
        // Send reply
        return message;
    }

    //client: recieved message from server for handling edit changes
    public Message editUpdate(Message msg) {
        log.info("Client: editUpdate");
        editUserList=msg.getFiles();
        //concurrent editing: apply editing info in local file
        FileHandler localFile = new FileHandler(CLIENT_EDIT_PATH);
        
        localFile.byteArrayToFile(msg.getBytes());
        log.info("Client: push change in "+filename +" successfully.");
        //loginPage.fileMngPage.document.apply(msg.getEditInfo());
        loginPage.fileMngPage.document.reReadFile();

        return null;
    }

    //server: recieved message from one client for quitting concurrent editing
    public Message editQuit(Message message) {
        log.info("Server: "+message.getUsername()+" is quitting");
        editFile.byteArrayToFile(message.getBytes());
        ServerCommunicator.concurrentEditClients.remove(ServerCommunicator.loginClients.get(message.getUsername()));
        editUserList.remove(message.getUsername());
        log.info("Server: New Concurrent editUserList: "+editUserList.toString());
        if(ServerCommunicator.concurrentEditClients.size()==0){
            log.info("Server: all clients quit the concurrent editing");
            originalFile.byteArrayToFile(editFile.fileToByteArray());
            editFile.getFile().delete();
            (new File(SERVER_EDIT_ROOTPATH)).delete();
            return null;
        } else{
            message.setStatus("final_update");
            message.setFiles(editUserList);
            return message;
        }
    }

    //client: someone quit and update the file
    public Message editFinalUpdate(Message message){
        log.info("Client: someone quit and update the file");
        loginPage.client.getEditHandler().editFile.byteArrayToFile(message.getBytes());
        return null;
    }
}
