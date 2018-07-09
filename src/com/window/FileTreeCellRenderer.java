package com.window;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import com.fileSystem.FileSystem;
import com.fileSystem.FileTypeEnum;
import javafx.util.Pair;
import java.util.List;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer{
    private FileSystem fileSystem;
    private String currentPath;
    private JTree jTree;
    private int childNumber;
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        FileNode selectedNode = (FileNode) value;
        tree = jTree;
        TreePath path = tree.getSelectionPath();
        if (expanded && sel) {
            if (!mainWindow.DELETING) {
                currentPath = handlePath(path.toString());

                selectedNode.removeAllChildren();
                updateTree(selectedNode);
            }
        }

            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
    //删除节点
    public void deleteNode(TreePath path){
        System.out.println(path.getParentPath().toString());
        currentPath = handleFilepath(path);
        FileNode parent = (FileNode) path.getParentPath().getLastPathComponent();
        System.out.println(parent.toString());
        parent.removeAllChildren();
        updateTree(parent);
        mainWindow.DELETING = false;
    }

    //更新树节点
    public void updateTree(FileNode selectedNode){
        List<Pair<String, FileTypeEnum>> nodeList = fileSystem.showDirectory(currentPath);
            for (Pair<String, FileTypeEnum> node : nodeList) {
                FileNode newNode = new FileNode(node.getKey());
                switch (node.getValue()) {
                    case INODE_IS_DIRECTORY: {
                        newNode.setType(0);
                    }break;
                    case INODE_IS_REGULAR_FILE: {
                        newNode.setType(1);
                    }break;
                }
                selectedNode.add(newNode);
        }
    }

    public void getChildNumber(int childNumber){
        this.childNumber = childNumber;
    }

    public FileTreeCellRenderer(FileSystem f, JTree tree){
        this.fileSystem = f;
        jTree = tree;
    }
    //带文件名的当前路径，一般添加文件使用
    public String handlePath(String path){
        String newPath;
        path = path.substring(1,path.length()-1);
        newPath = path.replaceAll(", ","/");
        //newPath = "/"+newPath;
        return newPath;
    }
    //不带文件名的当前路径
    public String handleFilepath(TreePath path){
        FileNode selectedNode = (FileNode) path.getLastPathComponent();
        String currentPath = handlePath(path.toString());
        currentPath = currentPath.substring(0,currentPath.length()-selectedNode.toString().length());
        System.out.println("this:"+currentPath);
        return currentPath;
    }
}
