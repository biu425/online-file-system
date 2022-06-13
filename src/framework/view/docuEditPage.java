package framework.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import client.com.*;
import server.com.ConcurrentEditHandler;
import server.com.FileHandler;;


public class docuEditPage {
    private int sequence;
    private String path;
    private Timer myTimer;
    private JTextArea textEditArea;
    public JScrollPane textScrollPanel;
    public JPanel editorPanel;
    public JPanel panel;
    public JButton returnButton;
    private static final int delay = 800;
    private Document docu;
    public int[] offset = {-1, -1};
    private String textString = "";
    public int editeMode = -1;
    private String fileName;
    private JLabel wordCountsLabel;
    private JLabel charCountsLabel;
    private int wordCounts;
    private int charCounts;
    private int row = 1;
    private int col = 1;
    private JLabel currRow;
    private JLabel currCol;
    public static boolean concurrentEditing = false;

    public docuEditPage(String docuPath, String fileName) {
        this.fileName = fileName;
        this.path = docuPath;
        initialize();
    };

    private ActionListener task = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            saveChanges();
            System.out.println("Save!");
        }
    };

    // pack the change information to editeInfo class
    private void saveChanges() {
        recount();
        EditInfo inf = new EditInfo();
        if (editeMode == 0) { // delete
            System.out.println("Delete: Start position: " + offset[0] + ". End position: " + offset[1]);
            inf.mode = 0;
            inf.startOffset = offset[0];
            inf.endOffset = offset[1];
        }
        else if (editeMode == 1) { // insert
            System.out.println("Insert: Start position: " + offset[0] + ". inserted string: " + textString);
            inf.mode = 1;
            inf.startOffset = offset[0];
            inf.text = textString;
        }
        offset[0] = -1;
        offset[1] = -1;
        textString = "";
        editeMode = -1;
        inf.sequence = ++sequence;
        // Send Edit Info to Server
        if(concurrentEditing){
            //concurrentEditing
            loginPage.client.getEditHandler().log.info("Client: push change");
            Message msg = new Message("concurrent_edit","pushChange",loginPage.user.getUsername());
            msg.setEditInfo(inf);
            loginPage.client.sendMsgToServer(msg);
        }else{
            //normal editing
            String tempstr= textEditArea.getText();
            FileHandler tempfile = new FileHandler(path);
            tempfile.writeTxtFile(tempstr);
            String tempath=(path.split("Users/"))[1];
            tempath=tempath.split((new File(path)).getName())[0];
            loginPage.client.upload(loginPage.user.getUsername(), loginPage.user.getPassword(), path, tempath);
        }
        

    }
    
    // apply changes to document
    public void apply(EditInfo i) {
        if (sequence >= i.sequence) return ;
        if (i.mode == 0) {
            try {
                docu.remove(i.startOffset, i.endOffset - i.startOffset);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                docu.insertString(i.startOffset, i.text, null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        sequence = i.sequence;
    }

    //count file contents size
    private void recount() {
        String c = textEditArea.getText();
        String words[] = c.split("\\s+");
        charCounts = c.length();
        wordCounts = words.length;
        charCountsLabel.setText(charCounts + " Characters");
        wordCountsLabel.setText(wordCounts + " Words");
    }

    private void relocate() {
        currRow.setText("Line: " + row);
        currCol.setText("Column: " + col);
    }

    private void refreshUserlist() {
        // Get Editing Users List From Server
        editorPanel.removeAll();
        List<String> userlist = loginPage.client.getUserList();
        if(userlist.size() == 0){
            userlist.add(loginPage.user.getUsername());
        }
        
        // refresh user 
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        String[] userArray = new String[userlist.size()];
        userlist.toArray(userArray);

        JLabel l = new JLabel("Current User", SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.PLAIN, 16));
        l.setPreferredSize(new Dimension(250, 20));

        JList<String> list = new JList<String>(userArray);
        list.setFont(new Font("Arial", Font.PLAIN, 16));
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        listPanel.add(l, BorderLayout.NORTH);
        listPanel.add(list);

        JPanel empty = new JPanel();
        empty.setPreferredSize(new Dimension(250, 30));
        JPanel fileInfoPanel = new JPanel();
        fileInfoPanel.setPreferredSize(new Dimension(250, 450));
        fileInfoPanel.setLayout(new BoxLayout(fileInfoPanel, BoxLayout.Y_AXIS));
        editorPanel.add(empty, BorderLayout.NORTH);
        editorPanel.add(listPanel, BorderLayout.CENTER);

        JLabel fileNameLabel = new JLabel("File Name: " + fileName);
        fileNameLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // Get Creator Name From Server
        String creator;
        if(loginPage.client.getEditHandler().owner==null) {
            creator=loginPage.user.getUsername();
        }else{
            creator=loginPage.client.getEditHandler().owner;
        }
        JLabel creatorLabel = new JLabel("Creator: " + creator); // get creater name
        creatorLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel title1 = new JLabel("This document totally have: ");
        title1.setFont(new Font("Arial", Font.PLAIN, 16));

        wordCountsLabel = new JLabel();
        wordCountsLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        charCountsLabel = new JLabel();
        charCountsLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        recount();

        JLabel title2 = new JLabel("Current position: ");
        title2.setFont(new Font("Arial", Font.PLAIN, 16));

        currRow = new JLabel();
        currRow.setFont(new Font("Arial", Font.PLAIN, 16));

        currCol = new JLabel();
        currCol.setFont(new Font("Arial", Font.PLAIN, 16));

        relocate();

        fileInfoPanel.add(Box.createRigidArea(new Dimension(1, 15)));
        fileInfoPanel.add(fileNameLabel);
        fileInfoPanel.add(Box.createRigidArea(new Dimension(1, 10)));
        fileInfoPanel.add(creatorLabel);
        fileInfoPanel.add(Box.createRigidArea(new Dimension(1, 10)));
        fileInfoPanel.add(title1);
        fileInfoPanel.add(Box.createRigidArea(new Dimension(1, 10)));
        fileInfoPanel.add(wordCountsLabel);
        fileInfoPanel.add(charCountsLabel);
        fileInfoPanel.add(Box.createRigidArea(new Dimension(1, 10)));
        fileInfoPanel.add(title2);
        fileInfoPanel.add(Box.createRigidArea(new Dimension(1, 10)));
        fileInfoPanel.add(currRow);
        fileInfoPanel.add(currCol);

        editorPanel.add(fileInfoPanel, BorderLayout.SOUTH);
    }

    private void initialize() {
        // syn sequence
        sequence = 0;
        
        textEditArea = new JTextArea();
        textEditArea.setFont(new Font("New Times Romam", Font.PLAIN, 16));

        myTimer = new Timer(delay, task);
        myTimer.setRepeats(false);

        textEditArea.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {
                if (myTimer.isRunning()) {
                    myTimer.stop();
                    System.out.println("timer stopped");
                }
                else {
                    System.out.println("timer not start");
                }
            }

            public void keyReleased(KeyEvent e) {
                myTimer.restart();
            }
        });

        readFile();

        // add caret listen to update caret position
        textEditArea.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                JTextArea editArea = (JTextArea)e.getSource();
                try {
                    int caretpos = editArea.getCaretPosition();
                    row = editArea.getLineOfOffset(caretpos);

                    col = caretpos - editArea.getLineStartOffset(row);
                    row += 1;
                }
                catch(Exception ex) { }
                relocate();
            }
        });

        docu = textEditArea.getDocument();
        docu.addDocumentListener(new DocumentListener() {
            public void removeUpdate(DocumentEvent e) {
                if(editeMode==2){return;}
                int startOffset = e.getOffset();
                int endOffset = e.getLength() + startOffset;
                System.out.println("start: " + startOffset + " end: " + endOffset);
                if (editeMode != -1 && editeMode != 0) {
                    // save change to class
                    saveChanges();
                }
                editeMode = 0;
                if (offset[1] == -1) {
                    offset[1] = endOffset;
                }
                offset[0] = startOffset;
            }

            public void insertUpdate(DocumentEvent e) {
                int startOffset = e.getOffset();
                int endOffset = e.getLength() + startOffset;
                String s = "";
                if(editeMode==2){return;}
                if (editeMode != -1 && editeMode != 1) {
                    // save change to class
                    saveChanges();
                }
                editeMode = 1;
                if (offset[0] == -1) {
                    offset[0] = startOffset;
                }
                offset[1] = endOffset;
                try {
                    s = docu.getText(startOffset, 1);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                textString += s;
                System.out.println("start: " + startOffset + " end: " + endOffset + " char: " + s);
            }

            public void changedUpdate(DocumentEvent e) {
                
            }
        });

        panel = new JPanel();
        panel.setPreferredSize(new Dimension(950, 50));
        panel.setVisible(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        returnButton = new JButton("Return");

        JButton inviteButton = new JButton("Invite");
        inviteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open Invitation Page
                invitationPage invite = new invitationPage(1, null, path);
                invite.frame.setVisible(true);
            }
        });

        editorPanel = new JPanel();
        editorPanel.setPreferredSize(new Dimension(250, 565));
        editorPanel.setLayout(new BorderLayout());
        editorPanel.setVisible(false);
        refreshUserlist();

        panel.add(returnButton);
        panel.add(inviteButton);

        textScrollPanel = new JScrollPane();
        textScrollPanel.setViewportView(textEditArea);
        textScrollPanel.setVisible(false); 
    }

    public void readFile(){
        File file = new File(path);
        FileReader reader;
        BufferedReader in;
        try {
            reader = new FileReader(file);
            in = new BufferedReader(reader);
            String s = in.readLine();
            while (s != null) {
                textEditArea.append(s + "\n");
                s = in.readLine();
            }
            in.close();
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reReadFile(){
        editeMode=2;
        row=-1;col=-1;
        offset[0]=-1;offset[1]=-1;
        FileHandler file = new FileHandler(path);
        byte[] bytes = file.fileToByteArray();
        textEditArea.setText(new String(bytes));
        editeMode=-1;
    }

    public void handleReturn(){
        if(concurrentEditing){
            loginPage.client.getEditHandler().log.info("Client: "+loginPage.user.getUsername()+" quit the concurrent editing");
            Message msg = new Message("concurrent_edit","quit",loginPage.user.getUsername());
            //copy bytes to server
            ConcurrentEditHandler editHandler=loginPage.client.getEditHandler();
            byte[] bytes=editHandler.editFile.fileToByteArray();
            msg.setBytes(bytes);
            //delete temporary file
            editHandler.editFile.getFile().delete();
            editHandler.editFile.getFile().getParentFile().delete();
            concurrentEditing=false;

            loginPage.client.sendMsgToServer(msg);

        }else{
            String tempath=(path.split("Users/"))[1];
            tempath=tempath.split((new File(path)).getName())[0];
            loginPage.client.upload(loginPage.user.getUsername(), loginPage.user.getPassword(), path, tempath);
        }
    }
}
