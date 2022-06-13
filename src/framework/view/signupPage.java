package framework.view;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;

public class signupPage implements FocusListener{
    public JPanel signupPanel;
    public JTextField username;
    public JPasswordField password;
    public JPasswordField password2;
    public JButton signupButton;
    public JButton returnButton;
    private JLabel signupLabel;
    private JLabel warningSign;
    private JLabel warningSign1;
    private JLabel warningSign2;


    public signupPage() {
        initialize();
    }

    private void initialize() {
        // sign-up panel initialize
        signupPanel = new JPanel();
        signupPanel.setBackground(Color.WHITE);
        signupPanel.setLayout(null);
        signupPanel.setSize(400,500);
        signupPanel.setLocation(400,0);

        // sign-up label initialize
        signupLabel = new JLabel("Sign Up");
        signupLabel.setFont((new Font("Arial", Font.PLAIN, 40)));
        signupLabel.setBounds(80, 70, 150, 55);
        signupLabel.setForeground(new Color(0, 180, 200));

        // username textfied initialize
        username = new JTextField();
        username.setBounds(80, 145, 240, 40);
        username.setFont(new Font("Arial", Font.PLAIN, 16));
        username.setForeground(Color.GRAY);
        username.setText("Enter username...");

        // password field initialize
        password = new JPasswordField();
        password.setBounds(80, 200, 240, 40);
        password.setFont(new Font("Arial", Font.PLAIN, 16));
        password.setText("Enter password...");
        password.setForeground(Color.GRAY);
        password.setEchoChar((char)0);
        
        // re-enter password field initialize
        password2 = new JPasswordField();
        password2.setBounds(80, 255, 240, 40);
        password2.setFont(new Font("Arial", Font.PLAIN, 16));
        password2.setText("Re-Enter password...");
        password2.setForeground(Color.GRAY);
        password2.setEchoChar((char)0);
        
        // warning sign label initialize - Length should not be less than 4 digits
        warningSign = new JLabel("Length should not be less than 4 digits.");
        warningSign.setForeground(Color.RED);
        warningSign.setBounds(80, 186, 300, 12);
        warningSign.setFont(new Font("Arial", Font.PLAIN, 12));
        warningSign.setVisible(false);

        // warning sign 1 label initialize - Length should not be less than 6 digits
        warningSign1 = new JLabel("Length should not be less than 6 digits.");
        warningSign1.setForeground(Color.RED);
        warningSign1.setBounds(80, 241, 300, 12);
        warningSign1.setFont(new Font("Arial", Font.PLAIN, 12));
        warningSign1.setVisible(false);

        // warning sign 2 label initialize - The passwords do not match!
        warningSign2 = new JLabel("The passwords do not match!");
        warningSign2.setForeground(Color.RED);
        warningSign2.setBounds(80, 296, 240, 12);
        warningSign2.setFont(new Font("Arial", Font.PLAIN, 12));
        warningSign2.setVisible(false);

        // sign up button initialize
        signupButton = new JButton("SignUp");
        signupButton.setFont(new Font("Arial", Font.PLAIN, 16));
        signupButton.setBounds(220, 320, 100, 40);

        // return button initialize
        returnButton = new JButton("Return");
        returnButton.setFont(new Font("Arial", Font.PLAIN, 16));
        returnButton.setBounds(80, 320, 100, 40);

        //add listeners for username,password,password2
        username.addFocusListener(this);
        password.addFocusListener(this);
        password2.addFocusListener(this);

        // add component to panel
        signupPanel.add(username);
        signupPanel.add(password);
        signupPanel.add(password2);
        signupPanel.add(signupButton);
        signupPanel.add(returnButton);
        signupPanel.add(signupLabel);
        signupPanel.add(warningSign);
        signupPanel.add(warningSign1);
        signupPanel.add(warningSign2);
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
            String passwordField=(new String(password.getPassword()));
            if (passwordField.equals("Enter password...")) {
                password.setEchoChar('●');
                password.setText("");
                password.setForeground(Color.BLACK);
            }
            else {
                password.selectAll();
            }
        } else if(e.getSource()==password2){
            String passwordField2=(new String(password2.getPassword()));
            if (passwordField2.equals("Re-Enter password...")) {
                password2.setEchoChar('●');
                password2.setText("");
                password2.setForeground(Color.BLACK);
            }
            else {
                password2.selectAll();
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(e.getSource()==username){
            if (username.getText().equals("")) {
                username.setText("Enter username...");
                username.setForeground(Color.GRAY);
            }
            else if (username.getText().length() < 4) {  // check username length
                username.setBorder(new LineBorder(Color.RED));
                warningSign.setVisible(true);
            }
            else {
                username.setBorder(new JTextField().getBorder());
                warningSign.setVisible(false);
            }
        } else if(e.getSource()==password){
            String passwordField=(new String(password.getPassword()));
            String passwordField2=(new String(password2.getPassword()));
            if (passwordField.equals("")) {
                password.setEchoChar((char)0);
                password.setText("Enter password...");
                password.setForeground(Color.GRAY);
            }
            else if (passwordField.length() < 6) { // check length
                password.setBorder(new LineBorder(Color.RED));
                warningSign1.setVisible(true);
            }
            else {
                password.setBorder(new JPasswordField().getBorder());
                warningSign1.setVisible(false);
                if (!passwordField2.equals("Re-Enter password...")) {
                    if (!passwordField2.equals(passwordField)) {
                        password2.setBorder(new LineBorder(Color.RED));
                        warningSign2.setVisible(true);
                    }
                }
            }
        } else if(e.getSource()==password2){
            String passwordField=(new String(password.getPassword()));
            String passwordField2=(new String(password2.getPassword()));
            if (passwordField2.equals("")) {
                password2.setEchoChar((char)0);
                password2.setText("Re-Enter password...");
                password2.setForeground(Color.GRAY);
            }
            else {
                if (!passwordField2.equals(passwordField)) {  // check if password is same
                    password2.setBorder(new LineBorder(Color.RED));
                    warningSign2.setVisible(true);
                }
                else {
                    password2.setBorder(new JPasswordField().getBorder());
                    warningSign2.setVisible(false);
                }
            }
        }
    }
}
