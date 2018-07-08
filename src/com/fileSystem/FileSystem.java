package com.fileSystem;


import com.externalStorage.ExternalStorage;
import com.internalStorage.InternalStorage;
import com.util.JSONLoader;
import com.util.JSONSaver;
import javafx.util.Pair;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FileSystem {//文件系统
    private ExternalStorage externalStorage;//调用磁盘操作的接口
    private InternalStorage internalStorage;//调用内存操作的接口
    private ArrayList<INode> iNodes;//iNode列表

    public FileSystem() {
        try {
            externalStorage = JSONLoader.getExternalStorageFromJson();
            internalStorage = new InternalStorage(100);
            iNodes = JSONLoader.getINodeArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getINodeNumberOfPath (String currentPath) {
        String[] pathArray = currentPath.split("/");
        int pathINodeNum = 0;//从根结点的INode编号开始向下寻找

        for (int i = 1; i < pathArray.length; i++) {
            pathINodeNum = iNodes.get(pathINodeNum).getPathMap().get(pathArray[i]);//定位当前路径所指示的INode的编号
        }
        return pathINodeNum;
    }

    public void newFile(String currentPath, String fileName) {//新建文件
        //初始化新INode，并将其加入列表
        INode newINode = new INode();
        newINode.setiNumber(iNodes.size());//当前iNode的编号为0~size-1，因此新结点的编号为size
        newINode.setFileName(fileName);
        newINode.setFileLength(0);
        newINode.setFileType(FileTypeEnum.INODE_IS_REGULAR_FILE);
        newINode.setDataBlockList(new ArrayList<>());
        iNodes.add(newINode);

        //为新文件在当前目录下注册
        int INodeNum = getINodeNumberOfPath(currentPath);
        iNodes.get(INodeNum).getPathMap().put(fileName, newINode.getiNumber());
    }

    public void newDirectory(String currentPath, String directoryName) {//新建目录
        // 初始化新INode，并将其加入列表
        INode newINode = new INode();
        newINode.setiNumber(iNodes.size());//当前iNode的编号为0~size-1，因此新结点的编号为size
        newINode.setFileName(directoryName);
        newINode.setFileLength(0);
        newINode.setFileType(FileTypeEnum.INODE_IS_DIRECTORY);
        newINode.setPathMap(new HashMap<>());
        iNodes.add(newINode);

        //为新文件在当前目录下注册
        int pathINodeNum = getINodeNumberOfPath(currentPath);
        iNodes.get(pathINodeNum).getPathMap().put(directoryName, newINode.getiNumber());
    }

    public String readFile(String currentPath, String fileName) {//读文件，返回值为以字符串表示的文件内容
        try {
            //先找到代表当前文件的INode
            int currentFileINodeNum = getINodeNumberOfPath(currentPath);//先确定本文件所在目录的INode
            currentFileINodeNum = iNodes.get(currentFileINodeNum).getPathMap().get(fileName);

            //得到磁盘上存储的文件数据
            byte[] fileData = externalStorage.getData(iNodes.get(currentFileINodeNum).getDataBlockList());
            return new String(fileData, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Exception!";
        }
    }

    public boolean writeFile(String currentPath, String fileName, String content) {//写文件，参数为本次写操作结束后的文件内容，返回值表示本次写操作是否成功；真为成功，假为失败
        try {
            //先找到代表当前文件的INode
            int currentFileINodeNum = getINodeNumberOfPath(currentPath);//先确定本文件所在目录的INode
            currentFileINodeNum = iNodes.get(currentFileINodeNum).getPathMap().get(fileName);

            //先释放原来占用的空间
            externalStorage.sfree(iNodes.get(currentFileINodeNum).getDataBlockList());

            //尝试为新内容申请空间
            byte[] contentByte = content.getBytes("utf-8");
            ArrayList<Integer> blockList = new ArrayList<>();
            externalStorage.salloc(contentByte.length, blockList);
            externalStorage.putData(contentByte, blockList);
            iNodes.get(currentFileINodeNum).setDataBlockList(blockList);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    public boolean copy(String sourceFileName, String currentPath, String targetPath) {//文件复制，把源文件复制到目标路径，返回真表示成功，返回假表示失败
//
//    }
//    public void move(String sourceFileName,String currentPath, String targetPath) {//文件移动，把源文件移动到目标路径
//
//    }
//    public void removeFile(String currentPath, String fileName) {//删除文件
//
//    }
//    public void removeDirectory(String currentPath, String directoryName) {//删除目录
//
//    }

    public List<Pair<String, FileTypeEnum>> showDirectory(String currentPath) {//根目录路径为 “~”；显示目录内容，参数为路径，返回值为列表；Pair中String参数表示文件或目录名，FileTypeEnum参数标识文件类型
        int pathINodeNum = getINodeNumberOfPath(currentPath);

        List<Pair<String, FileTypeEnum>> directoryList = new ArrayList<>();
        if (iNodes.get(pathINodeNum).getFileType() == FileTypeEnum.INODE_IS_DIRECTORY) { //如果当前路径指示的是目录，则返回其内部文件列表
            int childINodeNum;
            for (String key : iNodes.get(pathINodeNum).getPathMap().keySet()) {
                childINodeNum = iNodes.get(pathINodeNum).getPathMap().get(key);
                directoryList.add(new Pair<>(key, iNodes.get(childINodeNum).getFileType()));
            }
        }
        else //如果当前路径指示的是文件，则返回其自身的文件名和类型
            directoryList.add(new Pair<>(iNodes.get(pathINodeNum).getFileName(), iNodes.get(pathINodeNum).getFileType()));
        return directoryList;
    }

    public static void main(String[] args) throws IOException {
        FileSystem fileSystem = new FileSystem();
        String currentPath = "~";
        fileSystem.newFile(currentPath, "haha");
        fileSystem.writeFile(currentPath, "haha", "this is haha's content");
        System.out.println(fileSystem.readFile(currentPath, "haha"));
        JSONSaver.save(fileSystem.externalStorage, fileSystem.iNodes);
    }
}
