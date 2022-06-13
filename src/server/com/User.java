package server.com;

public class User {
    private String username="system";
    private String password="qwe123";

    //Constructor
    public User(){}

    // Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String toString(){
        String usermsg=username + "," + password;
        return usermsg;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
