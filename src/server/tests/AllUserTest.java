package server.tests;

import server.com.AllUser;
import server.com.User;

public class AllUserTest {
    public static void main(String[] args){
        AllUser users= new AllUser();
        users.addUser("system","qwe123");
        users.addUser("visitor","asd123");
        users.addUser("admin","zxc123");

        users.modUserPassword("visitor", "asd123", "123456");

        User result=users.getUser("visitor");
        System.out.println(result.getPassword().equals("asd123"));
    }
}
