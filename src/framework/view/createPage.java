package framework.view;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;
import client.com.*;

public class createPage implements ActionListener {
    private int mode;
    private String path;
    private String title;
    public JFrame mainFrame;
    private JTextField nameField;
    private JLabel txtLabel;
    private JLabel warningSign;
    public JButton confirmButton;
    private JButton cancelButton;

    private CurrentUser user;
    private ClientCommunicator client;

    public createPage(ClientCommunicator client, int mode, String currentPath,CurrentUser user) {
        this.user= user;
        this.mode= mode;
        this.client = client;
        this.path = currentPath;
        initialize();
    }

    public void initialize() {
        //file or folder name
        nameField = new JTextField();
        nameField.setBounds(55, 30, 240, 30);

        //txt file label
        txtLabel = new JLabel(".txt");
        txtLabel.setBounds(295, 30, 40, 30);
        txtLabel.setVisible(false);
        if(mode==0){
            title = "Create New Folder";
        } else{
            title = "Create New File";
            txtLabel.setVisible(true);
        }

        //Button: confirm, cancel
        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");
        confirmButton.setBounds(260,80,80, 25);
        cancelButton.setBounds(180,80,80, 25);

        //Button listeners
        //confirmButton.addActionListener(this);
        cancelButton.addActionListener(this);

        // warning sign label initialize - for create file/folder failed
        warningSign = new JLabel("Create failed!");
        warningSign.setForeground(Color.RED);
        warningSign.setBounds(55, 15, 180, 20);
        warningSign.setFont(new Font("Arial", Font.PLAIN, 15));
        warningSign.setVisible(false);

        mainFrame = new JFrame(title + " -- File Storage System");
        // mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(350,150);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(null);

        mainFrame.add(confirmButton);
        mainFrame.add(cancelButton);
        mainFrame.add(txtLabel);
        mainFrame.add(warningSign);
        mainFrame.add(nameField);
    }

    //Override
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand()=="Cancel"){
            mainFrame.removeAll();
            mainFrame.dispose();
        }
    }

    public void checkCreateFile(){
        String fileName = nameField.getText();
        if (mode == 1) {fileName += ".txt";}
        // send name and path to server and close frame
        boolean flag=client.createFileFolder(user.getUsername(),user.getPassword(), path+"/"+fileName);
        if(flag){
            mainFrame.removeAll();
            mainFrame.dispose();
        } else{
            warningSign.setVisible(true);
        }
    }
}
