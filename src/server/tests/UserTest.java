package server.tests;

import server.com.User;

public class UserTest {
    public static void main(String[] args){
        User[] users=new User[2];
        users[0]=new User();
        users[1]=new User("visitor","123456");

        System.out.println("User list: ");
        for(User u : users){
            System.out.println(u.toString());
        }

        users[1].setUsername("new_name");
        users[1].setPassword("new_password");
        System.out.println("New message of user1: "+users[1].toString());
    }
}
