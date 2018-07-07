package com.window;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class mainWindow extends JFrame{

    protected JTree fileTree;
    protected DefaultListModel fileModel;
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
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("我的电脑");
        fileTree = new JTree(root);
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

    public static void main(String[] args) {
        new mainWindow();
    }
}
