package framework.view;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import client.com.*;

import java.awt.Color;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;

public class fileManagerPage implements ActionListener {
    private static final String userImagePath = "./src/framework/images/user.png";
    private static final String rootPath="./src/client/Users/";
    private static final Icon docuIcon = new ImageIcon("./src/framework/images/docs.png");
    private static final Icon folderIcon = new ImageIcon("./src/framework/images/folder.png");
    private static final Icon addFile = new ImageIcon("./src/framework/images/create.png");
    private static final Icon addFolder = new ImageIcon("./src/framework/images/add-folder.png");
    private static final Icon uploadFile = new ImageIcon("./src/framework/images/upload.png");
    private static final Icon refreshIcon = new ImageIcon("./src/framework/images/refresh.png");
    private static final Font font16 = new Font("Arial", Font.PLAIN, 16);
    private String[] folderNames;
    public JFrame frame;
    private JPanel tmp;
    private JPanel fileTablePanel;
    private JPanel directPanel;
    private JComboBox<String> directMenu;
    private createPage createFrame;
    public docuEditPage document;
    private Image userImage;

    private String filePath;
    private CurrentUser onlineUser;
    private ClientCommunicator client;
    private JButton createFileButton;
    private JButton createFolderButton;
    private JButton uploadFileButton;
    private JButton refreshButton;
    private JButton logoutButton;
    private JButton settingButton;
    private JPanel userInfoPanel;
    private JPanel fileInfoPanel;

    public fileManagerPage(CurrentUser user,ClientCommunicator client,String filePath){
        this.filePath=filePath;
        this.onlineUser=user;
        this.client=client;
        initialize();
    }

    private void initialize() {
        //directPanel
        setDirectPanel();
        //fileTable
        fileTablePanel = new JPanel();
        fileTablePanel.setPreferredSize(new Dimension(950, 650));
        //view current folder contents
        refreshTable();

        //tmp
        tmp = new JPanel();
        tmp.setLayout(new BorderLayout());
        tmp.add(directPanel, BorderLayout.NORTH);
        tmp.add(fileTablePanel);

        //userInfoPenel
        userInfoPanel = setUserInfoPanel();

        //frame
        frame = new JFrame("File Management -- File Storage System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200,700);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(userInfoPanel, BorderLayout.WEST);
        frame.add(tmp);
    }

    public void setDirectPanel() {
        //directPanel
        directPanel = new JPanel();
        directPanel.setLayout(new BorderLayout());
        directPanel.setPreferredSize(new Dimension(950, 50));
        directPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        //createFileButton
        createFileButton = new JButton(addFile);
        createFolderButton = new JButton(addFolder);
        uploadFileButton = new JButton(uploadFile);
        refreshButton = new JButton(refreshIcon);
        //add listeners for buttons
        createFileButton.addActionListener(this);
        createFolderButton.addActionListener(this);
        uploadFileButton.addActionListener(this);
        refreshButton.addActionListener(this);
        //createButtonPanel
        JPanel createButtonPanel = new JPanel();
        createButtonPanel.setPreferredSize(new Dimension(200, 55));
        createButtonPanel.setLayout(new BoxLayout(createButtonPanel, BoxLayout.X_AXIS));
        createButtonPanel.add(refreshButton);
        createButtonPanel.add(createFileButton);
        createButtonPanel.add(createFolderButton);
        createButtonPanel.add(uploadFileButton);
        directPanel.add(createButtonPanel, BorderLayout.EAST);
    }

    public JPanel setInfoPanel(){
        //usernamePanel
        JPanel usernamePanel = new JPanel();
        usernamePanel.setPreferredSize(new Dimension(150, 40));
        usernamePanel.setLayout(new BorderLayout());
        //usernameLabel
        JLabel userNameLabel = new JLabel("User Name: ", SwingConstants.LEFT);
        userNameLabel.setFont(font16);
        userNameLabel.setPreferredSize(new Dimension(150, 18));
        //usernameLabel
        JLabel userName = new JLabel(onlineUser.getUsername(), SwingConstants.LEFT);
        userName.setFont(font16);
        userName.setPreferredSize(new Dimension(150, 18));
        //add
        usernamePanel.add(userNameLabel,BorderLayout.NORTH);
        usernamePanel.add(userName);

        //setting button and logoutButton panel
        JPanel logoutPanel = new JPanel();
        logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.Y_AXIS));
        //logout button
        logoutButton = new JButton("Log Out");
        logoutButton.setFont(font16);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMinimumSize(new Dimension(130, 20));
        logoutButton.setMaximumSize(new Dimension(130, 20));
        logoutButton.addActionListener(this);
        //setting button
        settingButton = new JButton("Setting");
        settingButton.setFont(font16);
        settingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingButton.setMinimumSize(new Dimension(130, 20));
        settingButton.setMaximumSize(new Dimension(130, 20));
        settingButton.addActionListener(this);
        //add
        logoutPanel.add(settingButton);
        logoutPanel.add(logoutButton);
        logoutPanel.add(Box.createRigidArea(new Dimension(130, 5)));

        //infoPanel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.add(usernamePanel, BorderLayout.NORTH);
        infoPanel.add(logoutPanel);
        return infoPanel;
    }

    public JPanel setUserInfoPanel() {
        //userInfoPanel
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setPreferredSize(new Dimension(250, 665));
        userInfoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        userInfoPanel.setLayout(new BorderLayout());

        //userCardPanel
        JPanel userCardInfo = new JPanel();
        userCardInfo.setLayout(new BorderLayout());
        userCardInfo.setPreferredSize(new Dimension(250, 100));

        //userImageLabel
        try {
            userImage = ImageIO.read(new File(userImagePath));
        } catch (IOException ioEx) {
            System.out.println("Error: Image Lost.");
        }
        Image newImage = userImage.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        JLabel userImageLabel = new JLabel(new ImageIcon(newImage));

        fileInfoPanel = new JPanel();
        fileInfoPanel.setPreferredSize(new Dimension(250, 450));
        fileInfoPanel.setLayout(new BoxLayout(fileInfoPanel, BoxLayout.Y_AXIS));

        //userInfoPanel
        JPanel infoPanel = setInfoPanel();
        userCardInfo.add(infoPanel);
        userCardInfo.add(userImageLabel, BorderLayout.WEST);
        userInfoPanel.add(userCardInfo, BorderLayout.NORTH);
        userInfoPanel.add(fileInfoPanel, BorderLayout.SOUTH);
        return userInfoPanel;
    }

    //ActionListener
    public void actionPerformed(ActionEvent e){
        JButton button = (JButton)e.getSource();
        if(button==createFileButton){
            createFrame = new createPage(client,1, filePath,onlineUser);
            createFrame.mainFrame.setVisible(true);
            createFrame.confirmButton.addActionListener(this);
        } else if(button==createFolderButton){
            createFrame = new createPage(client,0, filePath,onlineUser);
            createFrame.mainFrame.setVisible(true);
            createFrame.confirmButton.addActionListener(this);
        } else if(button==uploadFileButton){
            if(fileChooser()) refreshTable();
        } else if(button==logoutButton){
            client.logout(onlineUser.getUsername());
            frame.dispose();
            loginPage login=new loginPage();
            login.frame.setVisible(true);
        } else if(button==settingButton){
            settingPage setting=new settingPage(client,onlineUser);
            setting.frame.setVisible(true);
        } else if(button==refreshButton){
            refreshTable();
        } else if(createFrame!=null && button==createFrame.confirmButton){
            createFrame.checkCreateFile();
            refreshTable();
        }
    }

    //choose file to upload
    public boolean fileChooser(){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("./"));
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new FileNameExtensionFilter("text file(*.txt)", "txt"));
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            //************  send to server and upload file ************
            if(client.upload(onlineUser.getUsername(), onlineUser.getPassword(), file.getAbsolutePath(), filePath)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public void clearPanel() {
        document.editorPanel.setVisible(false);
        document.textScrollPanel.setVisible(false);
        document.panel.setVisible(false);
        document.editorPanel.removeAll();
        userInfoPanel.remove(document.editorPanel);
        tmp.remove(document.panel);
        tmp.remove(document.textScrollPanel);
    }

    public void openEditor(String docuPath) {
        System.out.println(docuPath);
        if (!docuPath.endsWith(".txt")) {
            JOptionPane.showMessageDialog(frame, "File extension not supported", "Error", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        String fileName=new File(docuPath).getName();
        if (document != null) {
            clearPanel();
        }
        document = new docuEditPage(docuPath,fileName);
        fileTablePanel.setVisible(false);
        directPanel.setVisible(false);
        fileInfoPanel.setVisible(false);
        //Return Button
        document.returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Close EditPage and reopen fileManagerPage
                clearPanel();
                fileTablePanel.setVisible(true);
                directPanel.setVisible(true);
                fileInfoPanel.setVisible(true);
                refreshTable();
                document.handleReturn();
            }
        });
        userInfoPanel.add(document.editorPanel);
        tmp.add(document.panel, BorderLayout.NORTH);
        tmp.add(document.textScrollPanel);
        document.editorPanel.setVisible(true);
        document.textScrollPanel.setVisible(true);
        document.panel.setVisible(true);
    }

    //refresh to get updated folder msg
    public void refreshTable(){
        File[] fileList = client.viewFolderContents(onlineUser.getUsername(), onlineUser.getPassword(), filePath);
        if (fileList==null){return;}
        
        JTable fileTable;
        JPopupMenu rightClickMenu;

        // check if tablePanel and directPanel has components. if have, remove
        clearFileDirPanel();
        // initialize directPanel, set JMenu according to current directory
        resetDirMenu();

        //show folder contents
        Object[][] data = new Object[fileList.length][5];
        String[] columnNames = {"", "Name", "Creator", "Last Modified", "Size"};

        if (fileList.length == 0) {
            fileTable = new JTable(data, columnNames);
            setEmptyPage();
        } else{
            for(int i=0; i<fileList.length; i++){
                data[i]=getFileMsg(fileList[i],loginPage.client.fileTime.get(i));
            }

            //set fileTable
            fileTable = new JTable(data, columnNames);
            fileTable.setRowHeight(30);
            fileTable.setFont(new Font("Arial", Font.PLAIN, 15));
            fileTable.setSelectionBackground(new Color(169, 213, 255));
            fileTable.setSelectionForeground(Color.BLACK);

            //rightClickMenu
            rightClickMenu = new JPopupMenu("Menu");
            rightClickMenu.add(new FileOpen(fileTable, fileList));
            rightClickMenu.addSeparator();
            rightClickMenu.add(new FileRefresh());
            rightClickMenu.addSeparator();
            rightClickMenu.add(new FileShare(fileTable, fileList));
            rightClickMenu.addSeparator();
            rightClickMenu.add(new FileDelete(fileTable, fileList));

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {return false;}

                public Class getColumnClass(int column) {
                    return getValueAt(0, column).getClass();
                }
            };

            fileTable.setModel(tableModel);
            fileTable.setFocusable(false);
            JTableHeader tableHeader = fileTable.getTableHeader();
            tableHeader.setPreferredSize(new Dimension(0, 30));
            tableHeader.setFont(new Font("Arial", Font.PLAIN, 15));
            TableColumnModel columnModel = fileTable.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(15);
            columnModel.getColumn(1).setPreferredWidth(400);
            columnModel.getColumn(2).setPreferredWidth(150);

            fileTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e) ) {
                        int row = fileTable.getSelectedRow();
                        System.out.println("Open file: " + fileTable.getValueAt(row, 1));
                        if (fileList[row].isDirectory()) { //open folder
                            filePath += "/" + fileTable.getValueAt(row, 1);
                            refreshTable();
                        } else { //open file
                            String tempPath = filePath + "/" + fileTable.getValueAt(row, 1);
                            client.downloadFile(onlineUser.getUsername(), onlineUser.getPassword(), tempPath);
                            openEditor(rootPath+tempPath);
                        }
                    } else if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
                        fileTable.clearSelection();
                        int selectedRow = fileTable.rowAtPoint(e.getPoint());
                        fileTable.addRowSelectionInterval(selectedRow, selectedRow);
                        rightClickMenu.show(fileTable, e.getX(), e.getY());
                    }else if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e) ) {
                        int row = fileTable.getSelectedRow();
                        File selectedFile = fileList[row];
                        System.out.println(selectedFile.getName() + " Selected!");
                        try {
                            refreshFileInfoPanel(selectedFile,row);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            fileTable.add(rightClickMenu);
            JScrollPane scrPane = new JScrollPane(fileTable);
            scrPane.setPreferredSize(new Dimension(950, 615));

            fileTablePanel.add(scrPane);
        }
        fileTablePanel.setVisible(true);
        directPanel.setVisible(true);

    }

    private void refreshFileInfoPanel(File f,int row) throws IOException {
        //fileInfoPanel=new JPanel();
        fileInfoPanel.setVisible(false);
        fileInfoPanel.removeAll();

        JLabel fileName = new JLabel("File Name: " + f.getName());
        fileName.setFont(new Font("Arial", Font.PLAIN, 16));

        // Read File Attribute From Server
        String creationTime=loginPage.client.fileTime.get(row);
        JLabel createTime = new JLabel("Creation Time: " + creationTime);
        createTime.setFont(new Font("Arial", Font.PLAIN, 16));

        // Get Creater (this will be shown on left side)
        JLabel creater = new JLabel("Creater: " + loginPage.user.getUsername());
        creater.setFont(new Font("Arial", Font.PLAIN, 16));

        fileInfoPanel.add(Box.createRigidArea(new Dimension(1, 15)));
        fileInfoPanel.add(fileName);
        fileInfoPanel.add(Box.createRigidArea(new Dimension(1, 10)));
        fileInfoPanel.add(createTime);
        fileInfoPanel.add(Box.createRigidArea(new Dimension(1, 10)));
        fileInfoPanel.add(creater);

        fileInfoPanel.setVisible(true);
    }

    public void clearFileDirPanel(){
        // check if tablePanel has components. if have, remove.
        Component[] tabComp = fileTablePanel.getComponents();
        if (tabComp.length > 0) {
            fileTablePanel.removeAll();
            fileTablePanel.setVisible(false);
        }

        // check if directPanel has components. if have, remove.
        Component[] dirComp = directPanel.getComponents();
        if (dirComp.length > 1) {
            directPanel.remove(directMenu);
            directPanel.setVisible(false);
        }
    }

    //reset Directory Menu for refresh folder contents
    public void resetDirMenu(){
        // initialize directPanel, set JMenu according to current directory
        File f = new File(rootPath+filePath);
        System.out.println(f);
        String[] path=(new File(rootPath+filePath)).getAbsolutePath().split("client/Users");
        //TODO: need change
        System.out.println(path[1]);
        folderNames = path[1].split("\\"+File.separator);

        directMenu = new JComboBox<String>();
        for (int i = folderNames.length - 1; i >= 1; i--) {
            directMenu.addItem(folderNames[i]);
        }
        // directMenu.setBackground(directPanel.getBackground());
        directMenu.setFont(new Font("Arial", Font.PLAIN, 15));
        directMenu.setPreferredSize(new Dimension(200, 40));
        directMenu.addItemListener(new ItemChangeListener());
        directPanel.add(directMenu, BorderLayout.WEST);
    }

    // directMenu item change listener
    public class ItemChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                String name = String.valueOf(item);
                String newPath = "";
                for (int i = 1; i < folderNames.length; i++) {
                    newPath += "/" + folderNames[i];
                    if (name == folderNames[i]) break;
                }
                filePath = newPath;
                refreshTable();
            }
        }
    }

    public void setEmptyPage(){
        JLabel noFileLabel;
        // show empty page
        noFileLabel = new JLabel("Empty...", SwingConstants.CENTER);
        noFileLabel.setBounds(0, 0, 950, 30);
        noFileLabel.setForeground(Color.GRAY);
        noFileLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        fileTablePanel.add(noFileLabel);
    }

    //get single file details
    public Object[] getFileMsg(File file,String fileTime){
        Path path = Paths.get(file.getAbsolutePath());
        long fileSize = 0;
        try {
            fileSize = Files.size(path);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String sizeString = bytesChange(fileSize);
        Icon icon = docuIcon;
        if (file.isDirectory()) icon = folderIcon;
        Object[] fileInfoList = {icon, file.getName(), "...", fileTime, sizeString};
        return fileInfoList;
    }

    //calculate the size of the file
    private String bytesChange(long size) {
        String res;
        int i = 0;
        String[] s = {"B", "KB", "MB", "GB"};

        for (i = 0; i < s.length; i++) {
            if (size >= 1024) {
                size /= 1024;
            }
            else break;
        }
        res = Long.toString(size) + " " + s[i];
        return res;
    }

    // open file in right click pop-up menu
    public class FileOpen extends AbstractAction {
        private JTable table;
        private File[] files;

        public FileOpen(JTable table, File[] fileList) {
            this.table = table;
            this.files = fileList;
            putValue(NAME, "Open");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            System.out.println("Open file: " + table.getValueAt(row, 1));
            if (files[row].isDirectory()) {
                filePath += "/" + table.getValueAt(row, 1);
                refreshTable();
            } else {
                // open document
                String tempPath = filePath + "/" + table.getValueAt(row, 1);
                client.downloadFile(onlineUser.getUsername(), onlineUser.getPassword(), tempPath);
                openEditor(rootPath+tempPath);
            }
        }
    }

    // update file in right click pop-up menu
    public class FileRefresh extends AbstractAction {
        public FileRefresh() {
            putValue(NAME, "Refresh");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Refresh. ");
            refreshTable();
        }
    }

    // delete file in right click pop-up menu
    public class FileDelete extends AbstractAction {
        private JTable table;

        public FileDelete(JTable table, File[] fileList) {
            this.table = table;
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            System.out.println("Delete file: " + table.getValueAt(row, 1));
            String tempPath = filePath + "/" + table.getValueAt(row, 1);
            client.deleteFileFolder(onlineUser.getUsername(), onlineUser.getPassword(), tempPath);
            refreshTable();
        }
    }

    // share file in right click pop-up menu
    public class FileShare extends AbstractAction {
        private JTable table;
        private File[] files;

        public FileShare(JTable table, File[] fileList) {
            this.table = table;
            this.files = fileList;
            putValue(NAME, "Share");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            System.out.println("Open file: " + table.getValueAt(row, 1));
            if (files[row].isFile()) {
                String tempPath = filePath + "/" + table.getValueAt(row, 1);
                sharePage share= new sharePage(client,onlineUser,tempPath);
                share.frame.setVisible(true);
            } 
        }
    }
}
