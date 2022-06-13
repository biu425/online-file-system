package client.com;

import java.util.List;
//import java.io.File;
import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L; //final means is a constant, unchangeable
	protected String action;
    protected String status;
    protected String username;
    protected String password;
    protected String text;
    protected String text2;
    protected EditInfo editInfo;
    protected List<String> fileTimes;
    protected List<String> files;
    protected byte[] txtfile;

    public Message() {
    }

    public Message(Message m) {
        action = m.action;
        status = m.status;
        username = m.username;
        password = m.password;
        text = m.text;
        text2 = m.text2;
        editInfo = new EditInfo(m.editInfo);
        files = m.files;
        txtfile = m.txtfile;
    }

    public Message(String action, String status, String username){
    	this.action = action;
    	this.status = status;
    	this.username = username;
    }
    
    public Message(String action, String status, String username, String password){
        this.action = action;
        this.status = status;
        this.username = username;
        this.password = password;
    }

    public Message(String action, String status, String username, String password, String text){
        this.action = action;
        this.status = status;
        this.username = username;
        this.password = password;
        this.text = text;
    }

    public Message(String action, String status, String username, String password, String text, String text2){
        this.action = action;
        this.status = status;
        this.username = username;
        this.password = password;
        this.text = text;
        this.text2 = text2;
    }

    public Message(String action, String status, String username, String password, String text,List<String> files){
        this.action = action;
        this.status = status;
        this.username = username;
        this.password = password;
        this.text = text;
        this.files = files;
    }

    public Message(String action, String status, String username, String password, String text,byte[] txtfile){
        this.action = action;
        this.status = status;
        this.username = username;
        this.password = password;
        this.text = text;
        this.txtfile= txtfile;
    }

    public Message(String action, String status, String username, String password, String text,String text2,byte[] txtfile){
        this.action = action;
        this.status = status;
        this.username = username;
        this.password = password;
        this.text = text;
        this.text2= text2;
        this.txtfile= txtfile;
    }

    public void setAction(String action){
        this.action = action;
    }

    public void setStatus(String status){
    	this.status = status;
    }

    public void setUsername(String username){
        this.username= username;
    }

    public void setPassword(String password){
        this.password= password;
    }

    public void setText(String text){
        this.text= text;
    }

    public void setText2(String text){
        this.text2= text;
    }

    public void setFiles(List<String> files){
        this.files = files;
    }
    
    public void setBytes(byte[] bytes){
        this.txtfile = bytes;
    }

    public void setEditInfo(EditInfo editInfo){
        this.editInfo = editInfo;
    }

    public void setFileTimes(List<String> fileTimes){
        this.fileTimes = fileTimes;
    }

    public String getAction(){
    	return action;
    }

    public String getStatus(){
    	return status;
    }

    public String getUsername(){
    	return username;
    }
    
    public String getPassword(){
    	return password;
    }

    public String getText(){
    	return text;
    }

    public String getText2(){
    	return text2;
    }

    public List<String> getFiles(){
        return files;
    }

    public byte[] getBytes(){
        return txtfile;
    }

    public EditInfo getEditInfo(){
        return editInfo;
    }

    public List<String> getFileTimes(){
        return fileTimes;
    }
}