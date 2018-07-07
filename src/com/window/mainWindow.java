package com.window;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    protected DefaultListModel fileModel;
    protected JPopupMenu popupMenu;
    protected JTextPane fileDisplay, commandLine;
    private List<DefaultMutableTreeNode> currentContent;

    public mainWindow(){
        this.setLayout(null);
        this.setSize(800,1000);
        this.setTitle("UNIX FileSystem");

        fileDisplay = new JTextPane();
        commandLine = new JTextPane();
        JLabel menu = new JLabel("目录");

        initFileTree();                 //初始化文件列表
        initButtons();
        popMenu();
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
        fileTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode node =(DefaultMutableTreeNode) value;
                if(node.isLeaf()){
                    setIcon(UIManager.getIcon("Tree.leafIcon"));
                }
                else {
                    if(expanded){
                        setIcon(UIManager.getIcon("Tree.openIcon"));
                    }
                    else{
                        setIcon(UIManager.getIcon("Tree.closedIcon"));
                    }
                }
                setText(node.toString());
                return this;
            }
        });
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                TreePath path = fileTree.getPathForLocation(e.getX(),e.getY());
                if(path == null){
                    return;
                }
                fileTree.setSelectionPath(path);

                if(e.getButton() == 3){
                    popupMenu.show(fileTree,e.getX(),e.getY());
                }

            }
        });
    }
    //初始化按钮组
    public void initButtons(){
        JButton editButton = new JButton("修改");
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileDisplay.setEditable(true);
                saveButton.setVisible(true);
                cancelButton.setVisible(true);
                editButton.setVisible(false);
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
                DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
                    ((DefaultTreeModel) fileTree.getModel()).insertNodeInto(new DefaultMutableTreeNode("新东西"),newNode,newNode.getChildCount());
                    fileTree.expandPath(fileTree.getSelectionPath());   //添加文件后扩展开文件夹
            }
        });

        //添加新文件夹
        addDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        //删除操作
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        popupMenu.add(addFile);
        popupMenu.add(addDirectory);
        popupMenu.add(deleteItem);

    }

    public static void main(String[] args) {
        new mainWindow();
    }
}
