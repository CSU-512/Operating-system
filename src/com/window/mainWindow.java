package com.window;
import com.exception.OSException;
import com.fileSystem.FilePrivilege;
import com.fileSystem.FileSystem;
import com.fileSystem.FileTypeEnum;
import com.fileSystem.INode;
import com.userManagement.User;
import com.userManagement.UserManagement;
import javafx.util.Pair;


import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class FileNode extends DefaultMutableTreeNode{
    private int type;      //文件类型，目录or文件，0为目录，1为文件
    @Override
    public boolean isLeaf() {
        if(type == 1){
            return true;
        }else{
            return false;
        }
    }
    public FileNode(String name){
        super(name);
    }
    public void setType(int i){
        type = i;
    }
}

public class mainWindow extends JFrame{
    private UserManagement userManagement;
    private User currentUser;
    private JTree fileTree;
    private JPopupMenu popupMenu;
    private JTextPane fileDisplay, commandLine;
    private FileSystem fileSystem;
    private static boolean DELETING = false;
    private TreePath pastePath;           //用于粘贴的地址
    private int pastePrefix = 0;              //粘贴前缀，无，复制，剪切
    private String commandPath = "~";
    private Set<String> keywords;

    public mainWindow(UserManagement userManagement, User currentUser){
        this.userManagement = userManagement;
        this.currentUser = currentUser;
        this.setLayout(null);
        this.setSize(1000,1000);
        this.setTitle("UNIX FileSystem  |  username: " + currentUser.getUserName() + "    UID: " + currentUser.getUID());
        this.setLocationRelativeTo(null);
        fileSystem = new FileSystem(userManagement, currentUser);
        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileDisplay = new JTextPane();
        commandLine = new JTextPane();
        JScrollPane jscForDisplay = new JScrollPane(fileDisplay);
        JScrollPane jscForCommand = new JScrollPane(commandLine);
        JLabel menuLabel = new JLabel("目录");
        JLabel contentLabel = new JLabel("文件内容");
        JLabel commandLabel = new JLabel("命令行");
        menuLabel.setFont(new Font("Courier",Font.ITALIC,20));
        contentLabel.setFont(new Font("Courier",Font.ITALIC,20));
        commandLabel.setFont(new Font("Courier",Font.ITALIC,20));

        initFileTree();                 //初始化文件列表
        initButtons();
        popMenu();
        initPane();
        initMenuBar();
        JScrollPane jScrollPane = new JScrollPane(fileTree);
        initDict();

        jScrollPane.setBounds(10,50,400,450);
        menuLabel.setBounds(10,10,200,50);
        contentLabel.setBounds(560,10,200,50);
        commandLabel.setBounds(50,540,200,50);
        jscForCommand.setBounds(40,600,900,300);
        jscForDisplay.setBounds(560,50,400,450);

        fileDisplay.setEditable(false);
        commandLine.setBackground(Color.GRAY);
        commandLine.setForeground(Color.WHITE);
        //命令行滑条不显示
        jscForCommand.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        this.add(jscForDisplay);
        this.add(jscForCommand);
        this.add(menuLabel);
        this.add(jScrollPane);
        this.add(contentLabel);
        this.add(commandLabel);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
        this.setVisible(true);
    }

    public void initFileTree(){
        String currentPath = "~/";

        List<Pair<String, FileTypeEnum>> nodeList = fileSystem.showDirectory(currentPath);
        FileNode root = new FileNode("~");
        root.setType(0);
        for(Pair<String, FileTypeEnum> node : nodeList){
            FileNode newNode = new FileNode(node.getKey());
            switch (node.getValue()){
                case INODE_IS_DIRECTORY: {
                    newNode.setType(0);
                } break;
                case INODE_IS_REGULAR_FILE:{
                    newNode.setType(1);
                } break;
            }
            root.add(newNode);
        }
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(root);
        fileTree = new JTree(defaultTreeModel);
        //tree渲染器
        DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();
        defaultTreeCellRenderer.setBackgroundSelectionColor(Color.GRAY);
        fileTree.setCellRenderer(defaultTreeCellRenderer);
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                TreePath path = fileTree.getPathForLocation(e.getX(),e.getY());
                if(path == null){
                    return;
                }
                fileTree.setSelectionPath(path);
                if(e.getButton() == 3){     //右键
                    popupMenu.show(fileTree,e.getX(),e.getY());
                }
            }
        });

        fileTree.addTreeSelectionListener(new TreeSelectionListener() {
            //选择节点触发
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                fileDisplay.setText("");
                TreePath treePath = fileTree.getSelectionPath();
                FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                if(!DELETING && (selectedNode != null &&!selectedNode.isLeaf())){
                    String currentPath = handlePath(treePath.toString());
                    List<Pair<String, FileTypeEnum>> nodeList = fileSystem.showDirectory(currentPath);
                    System.out.println(selectedNode.toString());
                    selectedNode.removeAllChildren();
                    System.out.println("能不能");
                    DefaultTreeModel defaultTreeModel1 = (DefaultTreeModel) fileTree.getModel();
                    if(nodeList.size() != selectedNode.getChildCount()){
                        for(Pair<String, FileTypeEnum> node : nodeList){
                            FileNode newNode = new FileNode(node.getKey());
                            switch (node.getValue()){
                                case INODE_IS_DIRECTORY: {
                                    newNode.setType(0);
                                } break;
                                case INODE_IS_REGULAR_FILE:{
                                    newNode.setType(1);
                                } break;
                            }
                            defaultTreeModel1.insertNodeInto(newNode,selectedNode,selectedNode.getChildCount());
                        }
                    }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        fileTree.updateUI();
                    }
                });
            }
        });
    }

    public void initMenuBar(){
        JMenuBar jMenuBar = new JMenuBar();
        JMenu jMenu = new JMenu("系统");
        JMenuItem jMenuItem = new JMenuItem("新建用户");
        JMenuItem checkUserItem = new JMenuItem("查看用户列表");
        this.setJMenuBar(jMenuBar);
        this.setResizable(false);
        jMenu.add(jMenuItem);
        jMenu.add(checkUserItem);
        jMenuBar.add(jMenu);

        jMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateUserDialog(currentUser,userManagement);
            }
        });

        checkUserItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserListDialog(userManagement.getUserList());
            }
        });
    }

    //初始化按钮组
    public void initButtons(){
        JButton editButton = new JButton("修改");
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        JButton openButton = new JButton("打开");
        JButton closeButton = new JButton("关闭");
        //点击修改触发事件
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath treePath = fileTree.getSelectionPath();
                String path = handlePath(treePath.toString());
                INode iNode = fileSystem.getINodeInfo(path);
                if(!FilePrivilege.isOKToDo('w', iNode, currentUser)) {
                    JOptionPane.showMessageDialog(new JFrame(),"权限不足");
                    return;
                }
                fileDisplay.setEditable(true);
                saveButton.setVisible(true);
                cancelButton.setVisible(true);
                editButton.setVisible(false);
            }
        });

        //保存
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                //selectedNode.setFileContent(fileDisplay.getText());
                fileDisplay.setEditable(false);
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
                editButton.setVisible(true);

                System.out.println(selectedNode.toString());
                TreePath treePath = fileTree.getSelectionPath();
                String currentPath = handleFilepath(treePath);
                System.out.println(currentPath);

                String path = handlePath(treePath.toString());
                INode iNode = fileSystem.getINodeInfo(path);
                if(FilePrivilege.isOKToDo('w', iNode, currentUser)) {
                    fileSystem.writeFile(currentPath, selectedNode.toString(), fileDisplay.getText());
                    fileSystem.saveCurrentFileSystem();
                }
                else{
                    JOptionPane.showMessageDialog(new JFrame(),"权限不足");
                }
            }
        });

        //取消修改
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileDisplay.setEditable(false);
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
                editButton.setVisible(true);
            }
        });

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath treePath = fileTree.getSelectionPath();
                FileNode selectedNode = (FileNode) treePath.getLastPathComponent();
                INode iNode = fileSystem.getINodeInfo(handlePath(treePath.toString()));
                if(!FilePrivilege.isOKToDo('r', iNode, currentUser)) {
                    JOptionPane.showMessageDialog(null,"权限不足");
                    return;
                }
                if(selectedNode != null && selectedNode.isLeaf())  {     //删除时触发两次会报错
                    String currentPath = handleFilepath(treePath);
                    String content = fileSystem.readFile(currentPath,selectedNode.toString());
                    fileDisplay.setText(content);
                }
                openButton.setEnabled(false);
                editButton.setVisible(true);
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editButton.setVisible(false);
                openButton.setEnabled(true);
                fileDisplay.setText("");
            }
        });
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        editButton.setVisible(false);
        editButton.setBounds(500,520,80,30);
        saveButton.setBounds(650,520,80,30);
        cancelButton.setBounds(800,520,80,30);
        openButton.setBounds(440,200,80,30);
        closeButton.setBounds(440,250,80,30);
        this.add(editButton);
        this.add(saveButton);
        this.add(cancelButton);
        this.add(openButton);
        this.add(closeButton);
    }

    //右键弹窗
    public void popMenu(){
        JMenuItem addFile, addDirectory, deleteItem, copyItem, cutItem, pasteItem,checkItem,changePrivilegeItem;

        popupMenu = new JPopupMenu();
        addFile = new JMenuItem("新建文件");
        addDirectory = new JMenuItem("新建文件夹");
        deleteItem = new JMenuItem("删除");
        copyItem = new JMenuItem("复制");
        cutItem = new JMenuItem("剪切");
        pasteItem = new JMenuItem("粘贴");
        checkItem = new JMenuItem("查看INODE");
        changePrivilegeItem = new JMenuItem("更改权限");

        //添加新文件
        addFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = JOptionPane.showInputDialog("输入文件名：");
                if(newName == null||newName.equals("")){
                    return;
                }
                FileNode newNode = new FileNode(newName);
                TreePath treePath = fileTree.getSelectionPath();
                newNode.setType(1);
                DefaultTreeModel defaultTreeModel = (DefaultTreeModel) fileTree.getModel();
                String currentPath = handlePath(treePath.toString());
                fileSystem.newFile(currentPath,newName);
                fileSystem.writeFile(currentPath,newName,"");
                FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                defaultTreeModel.insertNodeInto(newNode,selectedNode,selectedNode.getChildCount());
                fileSystem.saveCurrentFileSystem();
                System.out.println("expand"+treePath.toString());
                fileTree.expandPath(treePath);   //添加文件后扩展开文件夹
            }
        });

        //添加新文件夹
        addDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = JOptionPane.showInputDialog("输入文件夹名：");
                if(newName == null || newName.equals("")){
                    return;
                }
                FileNode newNode = new FileNode(newName);
                TreePath treePath = fileTree.getSelectionPath();
                newNode.setType(0);
                FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                if(selectedNode == null){
                    selectedNode = (FileNode) ((DefaultTreeModel) fileTree.getModel()).getRoot();
                }
                //添加新文件夹
                fileSystem.newDirectory(handlePath(treePath.toString()),newName);
                DefaultTreeModel defaultTreeModel = (DefaultTreeModel) fileTree.getModel();
                defaultTreeModel.insertNodeInto(newNode,selectedNode,selectedNode.getChildCount());
                fileSystem.saveCurrentFileSystem();
                fileTree.expandPath(treePath);   //添加文件夹后扩展开文件夹
            }
        });

        //删除操作
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DELETING = true;
                FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                DefaultTreeModel defaultTreeModel = (DefaultTreeModel) fileTree.getModel();
                //文件
                TreePath treePath = fileTree.getSelectionPath();
                String currentPath = handleFilepath(treePath);
                fileSystem.remove(currentPath,selectedNode.toString());
                fileSystem.saveCurrentFileSystem();
                defaultTreeModel.removeNodeFromParent(selectedNode);
                fileDisplay.setText("");
                DELETING = false;
            }
        });

        //复制
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.this.pastePath = fileTree.getSelectionPath();
                pastePrefix = 1;
            }
        });

        //剪切
        cutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.this.pastePath = fileTree.getSelectionPath();
                pastePrefix = 2;
            }
        });

        //粘贴
        pasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath startPath = pastePath;  //起始地址
                TreePath destPath = fileTree.getSelectionPath();    //目的地址
                FileNode copyNode,destNode = (FileNode) destPath.getLastPathComponent();
                String start,dest;
                DefaultTreeModel defaultTreeModel = (DefaultTreeModel) fileTree.getModel();

                switch (pastePrefix){
                    case 0:{}break;
                    case 1:{
                        start = handleFilepath(startPath);
                        dest = handlePath(destPath.toString());
                        copyNode = (FileNode) startPath.getLastPathComponent();
                        System.out.println("start:"+start+" dest:"+dest);
                        if(!fileSystem.copy(copyNode.toString(),start,dest)){
                            JOptionPane.showMessageDialog(new JFrame(),"文件重名，粘贴失败");
                            return;
                        }
                        FileNode newNode = (FileNode) copyNode.clone();
                        defaultTreeModel.insertNodeInto(newNode,destNode,destNode.getChildCount());
                    }break;
                    case 2:{
                        start = handleFilepath(startPath);
                        dest = handlePath(destPath.toString());
                        copyNode = (FileNode) startPath.getLastPathComponent();
                        System.out.println("start:"+start+" dest:"+dest);
                        if(!fileSystem.move(copyNode.toString(),start,dest)){
                            JOptionPane.showMessageDialog(new JFrame(),"文件重名，粘贴失败");
                            return;
                        }
                        defaultTreeModel.insertNodeInto(copyNode,destNode,destNode.getChildCount());
                    }break;
                }
                //pastePrefix = 0;
                fileSystem.saveCurrentFileSystem();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        fileTree.updateUI();
                    }
                });
            }
        });

        //查看INODE
        checkItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath treePath = fileTree.getSelectionPath();
                String current = handlePath(treePath.toString());
                INode iNode = fileSystem.getINodeInfo(current);
                new InodeInfoDialog(iNode.getFileLength(),iNode.getFileType(),iNode.getiNumber(),iNode.getPrivilege(),iNode.getAtime(),iNode.getMtime(),iNode.getCtime());
            }
        });

        changePrivilegeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath treePath = fileTree.getSelectionPath();
                //FileNode selectedNode = (FileNode) treePath.getLastPathComponent();
                String path = handlePath(treePath.toString());
                INode iNode = fileSystem.getINodeInfo(path);
                try {
                    new ChangePrivilegeDialog(iNode,currentUser,userManagement);
                } catch (OSException e1) {
                    e1.printStackTrace();
                }
            }
        });
        popupMenu.add(addFile);
        popupMenu.add(addDirectory);
        popupMenu.add(deleteItem);
        popupMenu.add(copyItem);
        popupMenu.add(cutItem);
        popupMenu.add(pasteItem);
        popupMenu.add(checkItem);
        popupMenu.add(changePrivilegeItem);
    }

    public void initDict(){
        keywords = new HashSet<>();
        keywords.add("touch");
        keywords.add("mkdir");
        keywords.add("cat");
        keywords.add("write");
        keywords.add("cp");
        keywords.add("mv");
        keywords.add("rm");
        keywords.add("rmdir");
        keywords.add("ls");
        keywords.add("clear");
        keywords.add("cd");
    }

    public void initPane(){
        fileDisplay.setFont(new Font("Courier",Font.BOLD,20));
        commandLine.setFont(new Font("Courier",Font.BOLD,20));
        commandLine.setText(currentUser.getUserName()+"     "+commandPath+"\n$ ");
        commandLine.getDocument().addDocumentListener(new Highlighter(commandLine));
        commandLine.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    String line[] = commandLine.getText().split("\n");
                    String command = line[line.length - 1];
                    System.out.println(command);
                    command = command.substring(2);
                    String firstWord = "";
                    for(String str : keywords){
                        if(command.startsWith(str)){
                            firstWord = str;
                            break;
                        }
                    }
                    //System.out.println(firstWord);
                    switch (firstWord){
                        case "touch":{
                            String currentPath = commandPath;
                            command = command.replace("touch","");
                            String newName = command.trim();
                            fileSystem.newFile(currentPath,newName);
                            fileSystem.writeFile(currentPath,newName,"");
                            fileSystem.saveCurrentFileSystem();
                            fileTree.updateUI();
                        }break;
                        case "mkdir":{
                            command = command.replace("mkdir","");
                            String newName = command.trim();
                            fileSystem.newDirectory(commandPath,newName);
                            fileSystem.saveCurrentFileSystem();
                            fileTree.updateUI();
                        }break;
                        case "cat":{

                        }break;
                        case "write":{

                        }break;
                        case "cp":{

                        }break;
                        case "mv":{

                        }break;
                        case "rm":{

                        }break;
                        case "rmdir":{

                        }break;
                        case "ls":{

                        }break;
                        case "clear":{
                            commandLine.setText("");
                        }break;
                        case "cd":{
                            command = command.replace("cd","");
                            command = command.trim();
                            commandPath = command;
                            System.out.println(commandPath);
                        }break;
                        default:{
                            commandLine.replaceSelection("\"" + command + "\" 不是一个正确的命令\n");
                        }break;
                    }

                    //换行添加“$”
                    commandLine.setCaretPosition(commandLine.getDocument().getLength());
                    commandLine.replaceSelection(currentUser.getUserName()+"     "+commandPath+"\n$ ");
                }
            }
        });
    }
    //处理路径（转换为底层需要的）
    public String handlePath(String path){
        String newPath;
        path = path.substring(1,path.length()-1);
        newPath = path.replaceAll(", ","/");
        if(newPath.charAt(newPath.length()-1) == '/'){
            newPath = newPath.substring(0,newPath.length()-1);
        }
        return newPath;
    }
    //专门处理文件需要的路径
    public String handleFilepath(TreePath path){
        FileNode selectedNode = (FileNode) path.getLastPathComponent();
        String currentPath = handlePath(path.toString());
        currentPath = currentPath.substring(0,currentPath.length()-selectedNode.toString().length());
        if(currentPath.charAt(currentPath.length()-1) == '/'){
            currentPath = currentPath.substring(0,currentPath.length()-1);
        }
        return currentPath;
    }

//    public static void main(String[] args) {
//        new mainWindow();
//    }
}
