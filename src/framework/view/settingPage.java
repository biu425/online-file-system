package framework.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import client.com.*;

public class settingPage {
    public JFrame frame;
    private CurrentUser user;
    private ClientCommunicator client;
    
    public settingPage(ClientCommunicator client,CurrentUser user) {
        this.client = client;
        this.user = user;
        initialize();
    }

    private void initialize() {
        //JPanel userNameSettingPanel = usernameSetting();
        JPanel passwdSettingPanel = passwdSetting();
        
        JTabbedPane settingPanel = new JTabbedPane();

        //settingPanel.add("Change Username", userNameSettingPanel);
        settingPanel.add("Change Password", passwdSettingPanel);

        frame = new JFrame("Account Setting");
        frame.setSize(300,180);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        
        frame.add(settingPanel);
    }

    public JPanel usernameSetting(){
        // user name setting page
        JPanel userNameSettingPanel = new JPanel();
        userNameSettingPanel.setLayout(new BoxLayout(userNameSettingPanel, BoxLayout.Y_AXIS));
        //label
        JLabel userNameLabel = new JLabel("Please enter your new username...");
        userNameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //testField
        JTextField userName = new JTextField();
        userName.setMinimumSize(new Dimension(300, 15));
        userName.setMaximumSize(new Dimension(300, 15));
        userName.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //button
        JButton userNameConfirm = new JButton("Comfirm");
        userNameConfirm.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //button listener
        userNameConfirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newUserName = userName.getText();
                if(client.changeAccount(user.getUsername(), user.getPassword(), newUserName, "username")){
                    user.setUsername(newUserName);
                    frame.dispose();
                } else{
                    userName.setBorder(new LineBorder(Color.RED, 1));
                }
            }
        });

        userNameSettingPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        userNameSettingPanel.add(userNameLabel);
        userNameSettingPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        userNameSettingPanel.add(userName);
        userNameSettingPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        userNameSettingPanel.add(userNameConfirm);
        return userNameSettingPanel;
    }

    public JPanel passwdSetting(){
        // password setting page
        JPanel passwdSettingPanel = new JPanel();
        passwdSettingPanel.setLayout(new BoxLayout(passwdSettingPanel, BoxLayout.Y_AXIS));
        //password1 label
        JLabel passwdLabel1 = new JLabel("Please enter your new password...");
        passwdLabel1.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //password1 field
        JPasswordField password1 = new JPasswordField();
        password1.setMinimumSize(new Dimension(300, 15));
        password1.setMaximumSize(new Dimension(300, 15));
        password1.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //password2 label
        JLabel passwdLabel2 = new JLabel("Please confirm your new password...");
        passwdLabel2.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //password2 field
        JPasswordField password2 = new JPasswordField();
        password2.setMinimumSize(new Dimension(300, 15));
        password2.setMaximumSize(new Dimension(300, 15));
        password2.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //button
        JButton passwordConfirm = new JButton("Comfirm");
        passwordConfirm.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //listener
        passwordConfirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pass1 = new String(password1.getPassword());
                String pass2 = new String(password2.getPassword());
                if (!pass1.equals("") && pass1.equals(pass2)) {
                    password1.setBorder(new JPasswordField().getBorder());
                    password2.setBorder(new JPasswordField().getBorder());
                    // ******** new password send to server ***********
                    if(client.changeAccount(user.getUsername(), user.getPassword(),pass1,"password")){
                        // close frame
                        user.setPassword(pass1);
                        frame.dispose();
                    }  
                } else {
                    password1.setBorder(new LineBorder(Color.RED, 1));
                    password2.setBorder(new LineBorder(Color.RED, 1));
                }  
            }
        });

        passwdSettingPanel.add(passwdLabel1);
        passwdSettingPanel.add(password1);
        passwdSettingPanel.add(passwdLabel2);
        passwdSettingPanel.add(password2);
        passwdSettingPanel.add(passwordConfirm);
        return passwdSettingPanel;
    }
}
