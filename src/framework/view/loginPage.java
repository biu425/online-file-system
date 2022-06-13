package framework.view;

import java.awt.event.*;
import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import client.com.*;

public class loginPage implements ActionListener,FocusListener{
    private String IP="127.0.0.";
    private static final String fileImagePath = "./src/framework/images/file.png";

    private signupPage signUp;
    public loadingPage loading;
    public JFrame frame;
    public JPanel loginPanel;
    private JPanel picPanel;
    public JTextField username;
    public JPasswordField password;
    public JButton loginButton;
    private JButton signupButton;
    public JLabel loginLabel;
    private BufferedImage fileImage;
    private JLabel imageLabel;
    private JLabel titleLabel; 
    private JLabel warningLabel;
    public static CurrentUser user;
    public static ClientCommunicator client;
    public static fileManagerPage fileMngPage;

    public loginPage(){
        IP += Integer.toString((int) (Math.random() * 9));
        IP = "172.31.177.155";
        client = new ClientCommunicator(IP);
        user = new CurrentUser();
        initialize();
    }

    private void initialize(){
        //initialize sign-up page (default visibility: false)
        signUp = new signupPage();
        signUp.signupPanel.setVisible(false);

        //initialize loading page (default visibility: false)
        loading = new loadingPage();
        loading.loadingPanel.setVisible(false);

        // log-in panel initialize (right part of the page)
        loginPanel = new JPanel();
        loginPanel.setBackground(Color.white);
        loginPanel.setLayout(null);
        loginPanel.setSize(400,500);
        loginPanel.setLocation(400,0);

        // picture panel initialize (left part of the page)
        picPanel = new JPanel();
        picPanel.setBackground(new Color(0,180,200));
        picPanel.setLayout(null);
        picPanel.setSize(400,500);
        picPanel.setLocation(0,0);

        // username textfield initialize
        username = new JTextField();
        username.setBounds(80, 160, 240, 40);
        username.setFont(new Font("Arial", Font.PLAIN, 16));
        username.setForeground(Color.GRAY);
        username.setText("Enter username...");

        // password filed initialize
        password = new JPasswordField();
        password.setBounds(80, 240, 240, 40);
        password.setFont(new Font("Arial", Font.PLAIN, 16));
        password.setText("Enter password...");
        password.setForeground(Color.GRAY);
        password.setEchoChar((char)0);

        // add listener to username textfield
        username.addFocusListener(this);
        // add listener to password field
        password.addFocusListener(this);

        // log-in button initialize
        loginButton = new JButton("Log In");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 16));
        loginButton.setBounds(80, 320, 100, 40);
        // log in button pushed
        loginButton.addActionListener(this);

        // log-in label initialize
        loginLabel = new JLabel("Log In");
        loginLabel.setFont((new Font("Arial", Font.PLAIN, 40)));
        loginLabel.setBounds(80, 70, 150, 55);
        loginLabel.setForeground(new Color(0, 180, 200));

        // sign-up button initialize
        signupButton = new JButton("Sign Up");
        signupButton.setFont(new Font("Arial", Font.PLAIN, 16));
        signupButton.setBounds(220, 320, 100, 40);
        // sign up button pushed
        signupButton.addActionListener(this);
        signUp.signupButton.addActionListener(this);
        signUp.returnButton.addActionListener(this);

        // loading image
        try {
            fileImage = ImageIO.read(new File(fileImagePath));
        } catch (IOException ioEx) {
            System.out.println("Error: Image Lost.");
        }
        Image newImage = fileImage.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        imageLabel = new JLabel(new ImageIcon(newImage));
        imageLabel.setBounds(100, 80, 200, 200);

        // title label initialize
        titleLabel = new JLabel("Online File Storage System", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 24));
        titleLabel.setBounds(50, 250, 300, 100);

        // warning label initialize (wrong username or password)
        warningLabel = new JLabel("Wrong username or password...");
        warningLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        warningLabel.setForeground(Color.RED);
        warningLabel.setBounds(80, 140, 200, 20);
        warningLabel.setVisible(false);
        
        // frame initialize
        frame = new JFrame("Login -- File Storage System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,500);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // add component to frame and panel
        frame.add(loginPanel);
        frame.add(signUp.signupPanel);
        frame.add(loading.loadingPanel);
        frame.add(picPanel);
        loginPanel.add(username);
        loginPanel.add(password);
        loginPanel.add(loginButton);
        loginPanel.add(signupButton);
        loginPanel.add(loginLabel);
        loginPanel.add(warningLabel);
        picPanel.add(imageLabel);
        picPanel.add(titleLabel);

        // frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("Log In")){
            //The method getText() from the type JPasswordField is deprecated
            String passwordField = new String(password.getPassword());
            if (!passwordField.equals("Enter password...") && !username.getText().equals("Enter username...")) {
                // send to server
                loginPanel.setVisible(false);
                loading.loadingPanel.setVisible(true);

                if (checkUser()) {
                    System.out.println(true);
                    frame.dispose();
                    fileMngPage = new fileManagerPage(user,client,user.getUsername());
                    fileMngPage.frame.setVisible(true);
                } else {
                    System.out.println("False");
                    loginPanel.setVisible(true);
                    loading.loadingPanel.setVisible(false);
                }
            }
        } else if(e.getActionCommand()=="SignUp"){
            String passwordField = new String(signUp.password.getPassword());
            String passwordField2= new String(signUp.password2.getPassword());
            if (signUp.username.getText().length() >= 4 && passwordField.length() >= 6 && passwordField.equals(passwordField2)) {
                // send username and password to server
                if(checkSignUp()){
                    System.out.println(true);
                    // close sign up panel and open log in panel
                    username.setText(signUp.username.getText());
                    username.setForeground(Color.BLACK);
                    password.setText(new String(signUp.password.getPassword()));
                    password.setEchoChar('●');
                    password.setForeground(Color.BLACK);
                    signUp.signupPanel.setVisible(false);
                    loginPanel.setVisible(true);
                } else{
                    System.out.println(false);
                    //waring msg:username existed
                }    
            }
        } else if(e.getActionCommand().equals("Sign Up")){
            //open sign up page
            loginPanel.setVisible(false);
            signUp.signupPanel.setVisible(true);
        } else if(e.getActionCommand()=="Return"){
            signUp.signupPanel.setVisible(false);
            loginPanel.setVisible(true);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if(e.getSource()==username){
            if (username.getText().equals("Enter username...")) {
                username.setText("");
                username.setForeground(Color.BLACK);
            }
            else {
                username.selectAll();
            }
        } else if(e.getSource()==password){
            if ((new String(password.getPassword())).equals("Enter password...")) {
                password.setEchoChar('●');
                password.setText("");
                password.setForeground(Color.BLACK);
            }
            else {
                password.selectAll();
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e){
        if(e.getSource()==username){
            if (username.getText().equals("")) {
                username.setText("Enter username...");
                username.setForeground(Color.GRAY);
            }
        }else if(e.getSource()==password){
            if ((new String(password.getPassword())).equals("")) {
                password.setEchoChar((char)0);
                password.setText("Enter password...");
                password.setForeground(Color.GRAY);
            }
        }
    }

    public boolean checkUser(){
        String passwd=new String(password.getPassword());
        user.setUsername(username.getText());
        user.setPassword(passwd);
        //Action: login
        if(client.loginSignup(user.getUsername(), user.getPassword(),"login")){
            return true;
        } else{
            warningLabel.setVisible(true);
            return false;
        }
    }

    public boolean checkSignUp(){
        String passwordField = new String(signUp.password.getPassword());
        user = new CurrentUser(signUp.username.getText(),passwordField);
        //Action: login
        if(client.loginSignup(user.getUsername(), user.getPassword(),"signup")){
            return true;
        } else{
            return false;
        }
    }
}
