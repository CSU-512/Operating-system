package com.window;
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
    private fFile file;
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
        file = new fFile();
    }
    public void setType(int i){
        type = i;
    }
    public String fileContent(){
        return file.getContent();
    }
    public void setFileContent(String content){
        file.setContent(content);
    }
}

public class mainWindow extends JFrame{
    protected JTree fileTree;
    protected DefaultListModel fileModel;
    protected JPopupMenu popupMenu;
    protected JTextPane fileDisplay, commandLine;
    private List<DefaultMutableTreeNode> currentContent;

    public mainWindow(){
        this.setLayout(null);
        this.setSize(800,1000);
        this.setTitle("UNIX FileSystem");
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileDisplay = new JTextPane();
        commandLine = new JTextPane();
        JLabel menu = new JLabel("目录");

        initFileTree();                 //初始化文件列表
        initButtons();
        popMenu();
        initPane();
        JScrollPane jScrollPane = new JScrollPane(fileTree);

        jScrollPane.setBounds(10,70,200,600);
        menu.setBounds(10,10,200,50);
        commandLine.setBounds(250,600,500,300);
        fileDisplay.setBounds(250,50,500,450);

        fileDisplay.setEditable(false);
        commandLine.setBackground(Color.GRAY);
        commandLine.setForeground(Color.WHITE);

        this.add(commandLine);
        this.add(fileDisplay);
        this.add(menu);
        this.add(jScrollPane);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void initFileTree(){
        FileNode root = new FileNode("我的电脑");
        FileNode menu = new FileNode("桌面");
        root.setType(0);
        menu.setType(0);
        root.add(menu);
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(root);
        fileTree = new JTree(defaultTreeModel);

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
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                if(selectedNode != null)        //删除时触发两次会报错
                    fileDisplay.setText(selectedNode.fileContent());
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
                selectedNode.setFileContent(fileDisplay.getText());
                fileDisplay.setEditable(false);
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
                editButton.setVisible(true);
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
                FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                String newName = JOptionPane.showInputDialog("输入文件名：");
                FileNode newNode = new FileNode(newName);
                newNode.setType(1);
                ((DefaultTreeModel) fileTree.getModel()).insertNodeInto(newNode,selectedNode,selectedNode.getChildCount());
                fileTree.expandPath(fileTree.getSelectionPath());   //添加文件后扩展开文件夹
            }
        });

        //添加新文件夹
        addDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                String newName = JOptionPane.showInputDialog("输入文件夹名：");
                FileNode newNode = new FileNode(newName);
                newNode.setType(0);
                ((DefaultTreeModel) fileTree.getModel()).insertNodeInto(newNode,selectedNode,selectedNode.getChildCount());
                fileTree.expandPath(fileTree.getSelectionPath());   //添加文件夹后扩展开文件夹
            }
        });

        //删除操作
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileNode selectedNode = (FileNode) fileTree.getLastSelectedPathComponent();
                DefaultTreeModel defaultTreeModel = (DefaultTreeModel) fileTree.getModel();
                //文件
                if(selectedNode.isLeaf()){
                    //调用底层函数删除
                    //。。。。。。
                    //删除界面上节点
                    //selectedNode.removeFromParent();
                    defaultTreeModel.removeNodeFromParent(selectedNode);
                    defaultTreeModel.reload();
                }
                else{
                    selectedNode.removeAllChildren();
                    defaultTreeModel.removeNodeFromParent(selectedNode);
                    defaultTreeModel.reload();
                }
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
        commandLine.getDocument().addDocumentListener(new Highlighter(commandLine));
        commandLine.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    commandLine.replaceSelection("$");
                }
            }

        });
    }

    public static void main(String[] args) {
        new mainWindow();
    }
}
