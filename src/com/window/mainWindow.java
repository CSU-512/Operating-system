package com.window;
import com.fileSystem.FileSystem;
import com.fileSystem.FileTypeEnum;
import com.util.JSONSaver;
import javafx.util.Pair;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    protected JTree fileTree;
    protected JPopupMenu popupMenu;
    protected JTextPane fileDisplay, commandLine;
    protected FileSystem fileSystem;
    public static boolean DELETING = false;

    public mainWindow(){
        this.setLayout(null);
        this.setSize(800,1000);
        this.setTitle("UNIX FileSystem");
        fileSystem = new FileSystem();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileDisplay = new JTextPane();
        commandLine = new JTextPane();
        JScrollPane jscForDisplay = new JScrollPane(fileDisplay);
        JScrollPane jscForCommand = new JScrollPane(commandLine);
        JLabel menuLabel = new JLabel("目录");
        JLabel contentLabel = new JLabel("文件内容");
        menuLabel.setFont(new Font("Courier",Font.ITALIC,20));
        contentLabel.setFont(new Font("Courier",Font.ITALIC,20));

        initFileTree();                 //初始化文件列表
        initButtons();
        popMenu();
        initPane();
        JScrollPane jScrollPane = new JScrollPane(fileTree);

        jScrollPane.setBounds(10,50,200,990);
        menuLabel.setBounds(10,10,200,50);
        contentLabel.setBounds(250,10,200,50);
        jscForCommand.setBounds(250,600,500,300);
        jscForDisplay.setBounds(250,50,500,450);

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

        DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();
        FileTreeCellRenderer fileTreeCellRenderer =new FileTreeCellRenderer(fileSystem);
        defaultTreeCellRenderer.setBackgroundSelectionColor(Color.GRAY);
        fileTreeCellRenderer.setBackgroundSelectionColor(Color.GRAY);

        fileTree.setCellRenderer(fileTreeCellRenderer);
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
                if(selectedNode != null && !selectedNode.equals(((DefaultTreeModel) fileTree.getModel()).getRoot()) && selectedNode.isLeaf())  {     //删除时触发两次会报错
                    String currentPath = handleFilepath(treePath);
                    String content = fileSystem.readFile(currentPath,selectedNode.toString());
                    fileDisplay.setText(content);
                }
            }
        });
    }

    //初始化按钮组
    public void initButtons(){
        JButton editButton = new JButton("修改");
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        //点击修改触发事件
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                fileSystem.writeFile(currentPath,selectedNode.toString(),fileDisplay.getText());
                fileSystem.saveCurrentFileSystem();
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

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        editButton.setBounds(250,520,80,30);
        saveButton.setBounds(450,520,80,30);
        cancelButton.setBounds(650,520,80,30);
        this.add(editButton);
        this.add(saveButton);
        this.add(cancelButton);
    }
    //右键弹窗
    public void popMenu(){
        JMenuItem addFile, addDirectory, deleteItem;

        popupMenu = new JPopupMenu();
        addFile = new JMenuItem("新建文件");
        addDirectory = new JMenuItem("新建文件夹");
        deleteItem = new JMenuItem("删除");
        //添加新文件
        addFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                String newName = JOptionPane.showInputDialog("输入文件名：");
                FileNode newNode = new FileNode(newName);
                TreePath treePath = fileTree.getSelectionPath();
                newNode.setType(1);

                String currentPath = handlePath(treePath.toString());
                fileSystem.newFile(currentPath,newName);
                fileSystem.writeFile(currentPath,newName,"");

                fileSystem.saveCurrentFileSystem();
                fileTree.updateUI();
                fileTree.expandPath(treePath);   //添加文件后扩展开文件夹
            }
        });

        //添加新文件夹
        addDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                String newName = JOptionPane.showInputDialog("输入文件夹名：");
                FileNode newNode = new FileNode(newName);
                TreePath treePath = fileTree.getSelectionPath();
                newNode.setType(0);
                //添加新文件夹
                fileSystem.newDirectory(handlePath(treePath.toString()),newName);

                fileSystem.saveCurrentFileSystem();
                fileTree.updateUI();
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
                System.out.println("remove:"+currentPath+" name:"+selectedNode.toString());
                fileSystem.remove(currentPath,selectedNode.toString());
                //defaultTreeModel.removeNodeFromParent(selectedNode);
                fileSystem.saveCurrentFileSystem();
                fileTree.updateUI();
                fileDisplay.setText("");
            }
        });
        popupMenu.add(addFile);
        popupMenu.add(addDirectory);
        popupMenu.add(deleteItem);
    }

    public void initPane(){
        fileDisplay.setFont(new Font("Courier",Font.BOLD,20));
        commandLine.setFont(new Font("Courier",Font.BOLD,20));
        commandLine.setText("$ ");
        commandLine.getDocument().addDocumentListener(new Highlighter(commandLine));
        commandLine.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    commandLine.setCaretPosition(commandLine.getDocument().getLength());
                    commandLine.replaceSelection("$ ");
                }
            }
        });
    }
    //处理路径（转换为底层需要的）
    public String handlePath(String path){
        String newPath;
        path = path.substring(1,path.length()-1);
        newPath = path.replaceAll(", ","/");
        return newPath;
    }
    //专门处理文件需要的路径
    public String handleFilepath(TreePath path){
        FileNode selectedNode = (FileNode) path.getLastPathComponent();
        String currentPath = handlePath(path.toString());
        currentPath = currentPath.substring(0,currentPath.length()-selectedNode.toString().length());
        System.out.println("this:"+currentPath);
        return currentPath;
    }

    public static void main(String[] args) {
        new mainWindow();
    }
}
