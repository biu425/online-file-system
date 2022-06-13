package framework.view;

import javax.swing.*;
import javax.swing.border.*;

import client.com.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


public class invitationPage {
    public JFrame frame;
    public JButton okButton;
    private JTextField field1;
    private JTextField field2;
    public Message msg;
    private String path;
    
    public invitationPage(int mode, Message msg, String path) {
        this.path = path;
        this.msg = msg;
        initialize(mode);
    }

    private void initialize(int i) {
        JPanel invitePanel = new JPanel();

        frame = new JFrame("Invite");

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // i == 1, invite other user
        if (i == 1) {
            frame.setSize(300,200);

            invitePanel.setLayout(new BoxLayout(invitePanel, BoxLayout.Y_AXIS));

            JLabel l1 = new JLabel("Send invitation to...");
            l1.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            JLabel l2 = new JLabel("Send invitation to...");
            l2.setAlignmentX(JComponent.CENTER_ALIGNMENT);

            field1 = new JTextField();
            field2 = new JTextField();
            field1.setMaximumSize(new Dimension(275, 20));
            field1.setMinimumSize(new Dimension(275, 20));
            field1.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            field2.setMaximumSize(new Dimension(275, 20));
            field2.setMinimumSize(new Dimension(275, 20));
            field2.setAlignmentX(JComponent.CENTER_ALIGNMENT);

            okButton = new JButton("OK");
            okButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String name1, name2;
                    if (field1.getText().equals("") && field2.getText().equals("")) {
                        field1.setBorder(new LineBorder(Color.RED, 1));
                        field2.setBorder(new LineBorder(Color.RED, 1));
                    }
                    else {
                        field1.setBorder(new JTextField().getBorder());
                        field2.setBorder(new JTextField().getBorder());
                        name1 = field1.getText();
                        name2 = field2.getText();
                        List<String> userlist = new ArrayList<>();
                        if (!name1.equals("")) userlist.add(name1);
                        if (!name2.equals("")) userlist.add(name2);
                        // send invitation to name1 and name2;
                        loginPage.client.getEditHandler().log.info("Client: Send Invitation To " + userlist.toString());
                        String filepath=(path.split("Users"))[1];
                        Message msg = new Message("concurrent_edit", "requesting", loginPage.user.getUsername(), loginPage.user.getPassword(), filepath, userlist);
                        loginPage.client.sendMsgToServer(msg);
                        frame.dispose();
                    }
                } 
            });
            invitePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            invitePanel.add(l1);
            invitePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            invitePanel.add(field1);
            invitePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            invitePanel.add(l2);
            invitePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            invitePanel.add(field2);
            invitePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            invitePanel.add(okButton);
        }
        // i != 1, being invited by others
        else {
            frame.setSize(300,150);

            invitePanel.setLayout(new BorderLayout());

            JLabel l = new JLabel("You are invited to edite document: " + msg.getText(), SwingConstants.CENTER);
            l.setFont(new Font("Arial", Font.PLAIN, 14));

            JPanel p = new JPanel();
            p.setPreferredSize(new Dimension(300, 50));
            p.setLayout(new FlowLayout());

            JButton accept = new JButton("Accept");
            JButton decline = new JButton("Decline");

            accept.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Accept Invitation
                    msg.setStatus("agree");
                    msg.setText2(msg.getUsername());
                    msg.setUsername(loginPage.user.getUsername());
                    loginPage.client.getEditHandler().log.info("Client: Accept Invitation.");
                    loginPage.client.sendMsgToServer(msg);
                }
            });

            decline.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Decline Invitation
                    msg.setStatus("reject");
                    msg.setUsername(loginPage.user.getUsername());
                    loginPage.client.getEditHandler().log.info("Client: Reject Invitation.");
                    loginPage.client.sendMsgToServer(msg);
                    frame.dispose();
                }
            });
            
            p.add(accept);
            p.add(decline);

            invitePanel.add(p, BorderLayout.SOUTH);
            invitePanel.add(l);
        }
        frame.add(invitePanel);
    }

    public static void main(String[] arg) {
        Message msg = new Message("1","1","1","1","tstsysysy");
        invitationPage p = new invitationPage(0, msg, "");
        p.frame.setVisible(true);
    }
}