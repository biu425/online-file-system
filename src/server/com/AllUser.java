package server.com;

import java.util.Map;
import java.util.HashMap;
import java.io.*;
import org.apache.logging.log4j.*;

public class AllUser {
    private Map<String,User> allUser = new HashMap<>();
    private static final String USER_PATH="src/server/user.txt";
    private Logger log=LogManager.getLogger(AllUser.class.getName());

    public User getUser(String username){
        return allUser.get(username);
    }

    public boolean isUser(String username){
        if(!allUser.containsKey(username)){
            return false;
        }
        return true;
    }

    public User validUser(String username, String password){
        User tempUser = getUser(username);
        if (tempUser==null || !tempUser.getPassword().equals(password)){
            return null;
        }
        else{
            return tempUser;
        }
    }

    public boolean addUser(String username, String password){
        if (!isUser(username)){
            allUser.put(username, new User(username, password));
            saveUser();
            return true;
        }
        else{
            return false;
        }
    }

    public boolean removeUser(String username, String password){
        User tempUser = validUser(username,password);
        if (tempUser==null){
            return false;
        }
        else{
            allUser.remove(username);
            saveUser();
            return true;
        }
    }

    public boolean modUserPassword(String username, String password, String new_password){
        User tempUser = validUser(username,password);
        if (tempUser==null){
            return false;
        }
        else{
            tempUser.setPassword(new_password);
            saveUser();
            return true;
        }
    }

    public boolean modUserName(String username, String password, String new_name){
        User tempUser = validUser(username,password);
        if (tempUser==null){
            return false;
        }
        else{
            tempUser.setUsername(new_name);
            saveUser();
            return true;
        }
    }

    public void saveUser(){
        File file = new File(USER_PATH);
        if(!file.exists()){
            try{
                file.createNewFile();
                //System.out.println("Server: Create user.txt successfully!");
            } catch (Exception e){
                log.error("Server: Create user.txt failed!");
            }
        }
        FileWriter f1=null;
        try{
            f1=new FileWriter(file);
            //System.out.println("Server: Open user.txt successfully!");
            if (!allUser.isEmpty()){
                for(User tempUser : allUser.values()){
                    f1.write(tempUser.toString());
                    f1.write(System.lineSeparator());
                }
            }
        } catch (Exception e){
            System.out.println(USER_PATH);
        }finally{
            try{
                f1.close();
            }catch (Exception e1){
                log.error("Server: Close user.txt failed!");
            }
        }
    }

    public void loadUserData(){
        File file=new File(USER_PATH);
        if(!file.exists()){
            try{
                file.createNewFile();
                //System.out.println("Server: Create user.txt successfully!");
            } catch (Exception e){
                log.error("Server: Create user.txt failed!");
            }
        }
        FileReader fin=null;
        BufferedReader f1=null;

        try{
            fin=new FileReader(file);
            f1=new BufferedReader(fin);
            String str=null;
            while((str=f1.readLine())!=null){
                String[] user_msg=str.split(",");
                addUser(user_msg[0],user_msg[1]);
            }
        }catch (Exception e){
            log.error("Server: Open file failed!");
        }finally{
            try{
                f1.close();
                fin.close();
            }catch(Exception e1){
                log.error("Server: Close file failed!");
            }
        }
    }
}
