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
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        FileNode selectedNode = (FileNode) value;
        if(expanded && selectedNode.equals(tree.getLastSelectedPathComponent())){
            TreePath path = tree.getSelectionPath();
            currentPath = handlePath(path.toString());
            System.out.println("path:"+currentPath);
            selectedNode.removeAllChildren();
            List<Pair<String, FileTypeEnum>> nodeList = fileSystem.showDirectory(currentPath);
            for (Pair<String, FileTypeEnum> node : nodeList) {
                System.out.println(node.toString());
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
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

    public FileTreeCellRenderer(FileSystem f){
        this.fileSystem = f;
    }

    public String handlePath(String path){
        String newPath;
        path = path.substring(1,path.length()-1);
        newPath = path.replaceAll(", ","/");
        //newPath = "/"+newPath;
        return newPath;
    }
}
