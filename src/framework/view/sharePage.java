package framework.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import client.com.*;

public class sharePage {
    public JFrame frame;
    private String filePath;
    private CurrentUser user;
    private ClientCommunicator client;

    public sharePage(ClientCommunicator client, CurrentUser user, String filePath) {
        this.client = client;
        this.user = user;
        this.filePath = filePath;
        initialize();
    }

    public void initialize() {
        JPanel sharePanel = new JPanel();
        sharePanel.setLayout(new BoxLayout(sharePanel, BoxLayout.Y_AXIS));

        //error message
        JLabel err = new JLabel("User not exist.");
        err.setVisible(false);
        //share message
        JLabel shareLabel = new JLabel("Share file to ...");
        shareLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        //share with
        JTextField name = new JTextField();
        name.setMinimumSize(new Dimension(275, 15));
        name.setMaximumSize(new Dimension(275, 15));
        name.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //button
        JButton confirm = new JButton("Confirm");
        confirm.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = name.getText();
                // if user name is not empty string and user exits in server
                if (!username.equals("")) {
                    if(client.shareFile(user.getUsername(), user.getPassword(), filePath, username)){
                        frame.dispose();
                    }else{
                        err.setVisible(true);
                        name.setBorder(new LineBorder(Color.RED, 1));
                    } 
                } else {
                    err.setVisible(true);
                    name.setBorder(new LineBorder(Color.RED, 1));
                }
            }
        });

        sharePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sharePanel.add(shareLabel);
        sharePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sharePanel.add(name);
        sharePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sharePanel.add(confirm);

        frame = new JFrame("Account Setting");
        frame.setSize(300,145);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        frame.add(sharePanel);
    }
}
