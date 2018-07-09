package com.fileSystem;


import com.exception.OSException;
import com.externalStorage.ExternalStorage;
import com.internalStorage.InternalStorage;
import com.userManagement.User;
import com.userManagement.UserManagement;
import com.userManagement.UserTypeEnum;
import com.util.JSONLoader;
import com.util.JSONSaver;
import javafx.util.Pair;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class FileSystem {//文件系统
    private UserManagement userManagement;
    private User currentUser;
    private ExternalStorage externalStorage;//调用磁盘操作的接口
    private InternalStorage internalStorage;//调用内存操作的接口
    private ArrayList<INode> iNodes;//iNode列表
    private Set<Integer> allocatedINodeNum;//已分配的inode编号集合

    public FileSystem() {
        try {
            externalStorage = JSONLoader.getExternalStorageFromJson();
            internalStorage = new InternalStorage(100);
            iNodes = JSONLoader.getINodeArray();
            allocatedINodeNum = new HashSet<>();
            for (INode iNode : iNodes)
                allocatedINodeNum.add(iNode.getiNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileSystem(UserManagement userManagement, User currentUser) {
        this.userManagement = userManagement;
        this.currentUser = currentUser;
        try {
            externalStorage = JSONLoader.getExternalStorageFromJson();
            internalStorage = new InternalStorage(100);
            iNodes = JSONLoader.getINodeArray();
            allocatedINodeNum = new HashSet<>();
            for (INode iNode : iNodes)
                allocatedINodeNum.add(iNode.getiNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-------------------私有函数接口---------------------

    //分配inode编号
    private int allocateINodeNum() {
        for (int i = 1; i < externalStorage.getSize(); i++)
            if (!allocatedINodeNum.contains(i)) {
                allocatedINodeNum.add(i);
                return i;
            }
        return -1;
    }

    //从inode编号得到结点在iNodes中的下标
    private int getIndexFromINodeNum(int INodeNum) {
        for (int i = 0; i < iNodes.size(); i++)
            if (iNodes.get(i).getiNumber() == INodeNum)
                return i;
        return -1;
    }

    //得到当前路径表示文件夹的inode编号
    private int getINodeNumberOfPath(String currentPath) {
        String[] pathArray = currentPath.split("/");

        int pathINodeNum = 0;//从根结点的INode编号开始向下寻找
        int index;

        for (int i = 1; i < pathArray.length; i++) {
            index = getIndexFromINodeNum(pathINodeNum);
            pathINodeNum = iNodes.get(index).getPathMap().get(pathArray[i]);//定位当前路径所指示的INode的编号
        }
        return pathINodeNum;
    }

    //更新所有父结点的文件大小，size为编号为baseINodeNum的inode的文件长度变化量（变化后-变化前），可正可负
    private void updateParentINodeFileLength(int baseINodeNum, int size) {
        int parentINodeNum = iNodes.get(getIndexFromINodeNum(baseINodeNum)).getParentINumber();
        int originSize, index;

        while (parentINodeNum != -1) {
            index = getIndexFromINodeNum(parentINodeNum);
            originSize = iNodes.get(index).getFileLength();
            iNodes.get(index).setFileLength(originSize + size);
            baseINodeNum = parentINodeNum;
            parentINodeNum = iNodes.get(getIndexFromINodeNum(baseINodeNum)).getParentINumber();
        }
    }

    //更新所有父结点的time信息，type可取'C','M','A'
    private void updateParentINodeTime(int baseINodeNum, char type) {
        int parentINodeNum = iNodes.get(getIndexFromINodeNum(baseINodeNum)).getParentINumber();
        int index;

        if (type == 'C') {
            while (parentINodeNum != -1) {
                index = getIndexFromINodeNum(parentINodeNum);
                iNodes.get(index).setCtime(new Date());
                baseINodeNum = parentINodeNum;
                parentINodeNum = iNodes.get(getIndexFromINodeNum(baseINodeNum)).getParentINumber();
            }
        }
        if (type == 'M') {
            while (parentINodeNum != -1) {
                index = getIndexFromINodeNum(parentINodeNum);
                iNodes.get(index).setMtime(new Date());
                iNodes.get(index).setCtime(new Date());//inode中的MTime项改变，则CTime改变
                baseINodeNum = parentINodeNum;
                parentINodeNum = iNodes.get(getIndexFromINodeNum(baseINodeNum)).getParentINumber();
            }
        }
        if (type == 'A') {
            while (parentINodeNum != -1) {
                index = getIndexFromINodeNum(parentINodeNum);
                iNodes.get(index).setAtime(new Date());
                iNodes.get(index).setCtime(new Date());//inode中的ATime项改变，则CTime改变
                baseINodeNum = parentINodeNum;
                parentINodeNum = iNodes.get(getIndexFromINodeNum(baseINodeNum)).getParentINumber();
            }
        }
    }

    //-------------------可调用的接口---------------------

    public boolean newFile(String currentPath, String fileName) {//新建文件，返回值表示是否新建成功，真为成功，假为失败
        int pathINodeNum = getINodeNumberOfPath(currentPath);
        int pathIndex = getIndexFromINodeNum(pathINodeNum);
        int newINodeNum;

        //先判断当前目录下是否有重名的文件，有则新建失败
        if (iNodes.get(pathIndex).getPathMap().containsKey(fileName))
            return false;
        else if ((newINodeNum = allocateINodeNum()) == -1)//如果新分得的inode编号为-1则新建失败
            return false;
        else {
            //初始化新INode，并将其加入列表
            INode newINode = new INode();
            newINode.setiNumber(newINodeNum);//为新结点设置分配好的inode编号
            newINode.setParentINumber(pathINodeNum);//设置父结点编号
            newINode.setFileName(fileName);
            newINode.setFileLength(0);//初始文件长度为0
            newINode.setFileType(FileTypeEnum.INODE_IS_REGULAR_FILE);
            //设置文件所有者信息
            newINode.setUserID(currentUser.getUID());
            try {
                //初始权限
                //superuser：rwv--v--v
                //user：rwvrwv--v
                //guest：rwvrwvrwv
                if (currentUser.getUserType() == UserTypeEnum.OS_SUPERUSER)
                    newINode.setPrivilege(currentUser, userManagement, FilePrivilege.stringToPrivilege("rwv--v--v"));
                if (currentUser.getUserType() == UserTypeEnum.OS_USER)
                    newINode.setPrivilege(currentUser, userManagement, FilePrivilege.stringToPrivilege("rwvrwv--v"));
                if (currentUser.getUserType() == UserTypeEnum.OS_GUEST)
                    newINode.setPrivilege(currentUser, userManagement, FilePrivilege.stringToPrivilege("rwvrwvrwv"));
            } catch (OSException e) {
                e.printExceptionMessage();
                return false;
            }
            //初始化各个时间
            newINode.setCtime(new Date());
            newINode.setMtime(new Date());
            newINode.setAtime(new Date());
            newINode.setDataBlockList(new ArrayList<>());//初始化数据块列表
            iNodes.add(newINode);

            //为新文件在当前目录下注册
            iNodes.get(pathIndex).getPathMap().put(fileName, newINode.getiNumber());
            return true;
        }
    }

    public boolean newDirectory(String currentPath, String directoryName) {//新建目录，返回值表示是否新建成功，真为成功，假为失败
        int pathINodeNum = getINodeNumberOfPath(currentPath);
        int pathIndex = getIndexFromINodeNum(pathINodeNum);
        int newINodeNum;

        //先判断当前目录下是否有重名的文件，有则新建失败
        if (iNodes.get(pathIndex).getPathMap().containsKey(directoryName))
            return false;
        else if ((newINodeNum = allocateINodeNum()) == -1)//如果新分得的inode编号为-1则新建失败
            return false;
        else {
            // 初始化新INode，并将其加入列表
            INode newINode = new INode();
            newINode.setiNumber(newINodeNum);//为新结点设置分配好的inode编号
            newINode.setParentINumber(pathINodeNum);//设置父结点编号
            newINode.setFileName(directoryName);
            newINode.setFileLength(0);
            newINode.setFileType(FileTypeEnum.INODE_IS_DIRECTORY);
            //设置文件所有者信息
            newINode.setUserID(currentUser.getUID());
            try {
                //初始权限
                //superuser：rwv--v--v
                //user：rwvrwv--v
                //guest：rwvrwvrwv
                if (currentUser.getUserType() == UserTypeEnum.OS_SUPERUSER)
                    newINode.setPrivilege(currentUser, userManagement, FilePrivilege.stringToPrivilege("rwv--v--v"));
                if (currentUser.getUserType() == UserTypeEnum.OS_USER)
                    newINode.setPrivilege(currentUser, userManagement, FilePrivilege.stringToPrivilege("rwvrwv--v"));
                if (currentUser.getUserType() == UserTypeEnum.OS_GUEST)
                    newINode.setPrivilege(currentUser, userManagement, FilePrivilege.stringToPrivilege("rwvrwvrwv"));
            } catch (OSException e) {
                e.printExceptionMessage();
                return false;
            }
            //初始化各个时间
            newINode.setCtime(new Date());
            newINode.setMtime(new Date());
            newINode.setAtime(new Date());
            newINode.setPathMap(new HashMap<>());//初始化子结点映射
            iNodes.add(newINode);

            //为新文件在当前目录下注册
            iNodes.get(pathIndex).getPathMap().put(directoryName, newINode.getiNumber());
            return true;
        }
    }

    public String readFile(String currentPath, String fileName) {//读文件，返回值为以字符串表示的文件内容
        try {
            //先找到代表当前文件的INode和其在iNodes中的索引位置
            int currentFileINodeNum = getINodeNumberOfPath(currentPath);//先确定本文件所在目录的INode
            currentFileINodeNum = iNodes.get(getIndexFromINodeNum(currentFileINodeNum)).getPathMap().get(fileName);
            int currentFileIndex = getIndexFromINodeNum(currentFileINodeNum);

            //得到磁盘上存储的文件数据
            byte[] fileData = externalStorage.getData(iNodes.get(currentFileIndex).getDataBlockList());

            iNodes.get(currentFileIndex).setAtime(new Date());//修改当前文件的打开时间
            updateParentINodeTime(currentFileINodeNum, 'A');//修改父结点目录的的打开时间

            return new String(fileData, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Exception!";
        }
    }

    public boolean writeFile(String currentPath, String fileName, String content) {//写文件，参数为本次写操作结束后的文件内容，返回值表示本次写操作是否成功；真为成功，假为失败
        try {
            //先找到代表当前文件的INode和其在iNodes中的索引位置
            int currentFileINodeNum = getINodeNumberOfPath(currentPath);//先确定本文件所在目录的INode
            System.out.println(iNodes.get(getIndexFromINodeNum(currentFileINodeNum)).getPathMap());
            currentFileINodeNum = iNodes.get(getIndexFromINodeNum(currentFileINodeNum)).getPathMap().get(fileName);
            int currentFileIndex = getIndexFromINodeNum(currentFileINodeNum);

            //保存原文件大小
            int initSize = iNodes.get(currentFileIndex).getFileLength();

            //先释放原来占用的空间
            externalStorage.sfree(iNodes.get(currentFileIndex).getDataBlockList());

            //尝试为新内容申请空间
            byte[] contentByte = content.getBytes("utf-8");
            ArrayList<Integer> blockList = new ArrayList<>();
            externalStorage.salloc(contentByte.length, blockList);
            externalStorage.putData(contentByte, blockList);
            iNodes.get(currentFileIndex).setDataBlockList(blockList);//设置新的分配块

            iNodes.get(currentFileIndex).setFileLength(contentByte.length);//设置文件大小
            updateParentINodeFileLength(currentFileINodeNum, contentByte.length - initSize);//更新父结点文件大小域

            iNodes.get(currentFileIndex).setMtime(new Date());//修改当前文件的修改时间
            updateParentINodeTime(currentFileINodeNum, 'M');//修改父结点目录的的修改时间

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean copy(String sourceFileName, String currentPath, String targetPath) {//文件复制，把源文件复制到目标路径，返回真表示成功，返回假表示失败
        //先找到代表当前文件的INode和其在iNodes中的索引位置
        int currentFileINodeNum = getINodeNumberOfPath(currentPath);//先确定本文件所在目录的INode
        currentFileINodeNum = iNodes.get(getIndexFromINodeNum(currentFileINodeNum)).getPathMap().get(sourceFileName);
        int currentFileIndex = getIndexFromINodeNum(currentFileINodeNum);

        //判断磁盘剩余空间是否还够本次复制
        int fileSize = iNodes.get(currentFileIndex).getFileLength();
        int remainingDiskSize = (externalStorage.getSize() - externalStorage.getInUse()) * externalStorage.getBlockSize();

        if (fileSize <= remainingDiskSize) {//空间足够才尝试执行复制
            //如果sourceFile是目录，则递归复制
            if (iNodes.get(currentFileIndex).getFileType() == FileTypeEnum.INODE_IS_DIRECTORY) {
                String directoryName = "/" + sourceFileName;
                //先尝试在目标目录建立新子目录,substr是去掉之前加入的"/"
                if (newDirectory(targetPath, directoryName.substring(1))) {
                    for (String key : iNodes.get(currentFileIndex).getPathMap().keySet())
                        copy(key, currentPath + directoryName, targetPath + directoryName);
                    return true;
                }
                return false;
            }
            else {//否则说明sourceFile是普通文件，可以直接复制
                byte[] contentByte = externalStorage.getData(iNodes.get(currentFileIndex).getDataBlockList());
                if (newFile(targetPath, sourceFileName)) {
                    try {
                        writeFile(targetPath, sourceFileName, new String(contentByte, "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean move(String sourceFileName,String currentPath, String targetPath) {//文件移动，把源文件移动到目标路径，返回真表示成功，返回假表示失败
        //先找到代表当前文件的INode和其在iNodes中的索引位置
        if (copy(sourceFileName, currentPath, targetPath)) {
            remove(currentPath, sourceFileName);
            return true;
        }
        return false;
    }

    public void remove(String currentPath, String sourceFileName) {//删除文件
        //回收磁盘空间、从iNodes中移除、清除父结点信息

        //先找到代表当前文件的INode和其在iNodes中的索引位置
        int currentFileINodeNum = getINodeNumberOfPath(currentPath);//先确定本文件所在目录的INode
        currentFileINodeNum = iNodes.get(getIndexFromINodeNum(currentFileINodeNum)).getPathMap().get(sourceFileName);
        int currentFileIndex = getIndexFromINodeNum(currentFileINodeNum);

        //不允许删除根结点
        int parentINodeNum = iNodes.get(currentFileIndex).getParentINumber();
        if (parentINodeNum == -1) {//不允许删除根节结点
            System.out.println("不允许删除根结点");
            return;
        }

        //记录下当前文件的inode编号，后续执行删除操作
        int INodeNum = iNodes.get(currentFileIndex).getiNumber();

        //如果sourceFile是目录，则递归回收空间
        if (iNodes.get(currentFileIndex).getFileType() == FileTypeEnum.INODE_IS_DIRECTORY) {
            String directoryName = "/" + sourceFileName;

            //执行递归时，里层递归会利用***标识的语句修改当前对象的pathMap，即边遍历集合，边修改集合，会导致ConcurrentModificationException
            //为了防止ConcurrentModificationException，用一个临时map存储待删文件的pathMap
            Map<String, Integer> tempMap = new HashMap<>(iNodes.get(currentFileIndex).getPathMap());

            //递归删除
            for (String key : tempMap.keySet())
                remove(currentPath + directoryName, key);
        }
        else //否则说明sourceFile是普通文件，可以直接回收空间
            externalStorage.sfree(iNodes.get(currentFileIndex).getDataBlockList());

        //***删除父节点Map中的信息***
        iNodes.get(getIndexFromINodeNum(parentINodeNum)).getPathMap().remove(sourceFileName);

        //更新父结点的文件大小
        updateParentINodeFileLength(currentFileINodeNum, 0 - iNodes.get(currentFileIndex).getFileLength());

        //更新父结点的修改时间
        updateParentINodeTime(currentFileINodeNum, 'M');

        //回收inode编号、从iNodes中移除结点
        allocatedINodeNum.remove(INodeNum);
        for (int i = 0; i < iNodes.size(); i++)
            if (iNodes.get(i).getiNumber() == INodeNum) {
                System.out.println(iNodes.get(i).getFileName());
                iNodes.remove(i);
                break;
            }
    }

    public List<Pair<String, FileTypeEnum>> showDirectory(String currentPath) {//根目录路径为 "~"；显示目录内容，参数为路径，返回值为列表；Pair中String参数表示文件或目录名，FileTypeEnum参数标识文件类型
        int pathINodeNum = getINodeNumberOfPath(currentPath);
        int pathIndex = getIndexFromINodeNum(pathINodeNum);

        List<Pair<String, FileTypeEnum>> directoryList = new ArrayList<>();

        System.out.println(FilePrivilege.privilegeToString(iNodes.get(pathIndex).getPrivilege()));

        if (!FilePrivilege.isOKToDo('v', iNodes.get(pathIndex), currentUser)) {//如果当前路径用户都不可见则直接返回空列表
            //System.out.println(FilePrivilege.privilegeToString(iNodes.get(pathIndex).getPrivilege()));
            return directoryList;
        }

        if (iNodes.get(pathIndex).getFileType() == FileTypeEnum.INODE_IS_DIRECTORY) {//如果当前路径指示的是目录，则返回其内部文件列表
            int childINodeNum, childIndex;
            for (String key : iNodes.get(pathIndex).getPathMap().keySet()) {
                childINodeNum = iNodes.get(pathIndex).getPathMap().get(key);
                childIndex = getIndexFromINodeNum(childINodeNum);
                //只有对用户可见的文件才会加入列表
                if (FilePrivilege.isOKToDo('v', iNodes.get(childIndex), currentUser))
                    directoryList.add(new Pair<>(key, iNodes.get(childIndex).getFileType()));
            }
        }
        else //如果当前路径指示的是文件，则返回其自身的文件名和类型
            directoryList.add(new Pair<>(iNodes.get(pathIndex).getFileName(), iNodes.get(pathIndex).getFileType()));
        return directoryList;
    }

    public INode getINodeInfo(String filePath) {//返回给定目录的inode信息，filePath表示文件绝对路径，如查看文件haha的INode信息则输入："~/dir/haha"
        int pathINodeNum = getINodeNumberOfPath(filePath);
        return iNodes.get(getIndexFromINodeNum(pathINodeNum));
    }

    public boolean changePrivilege(String filePath, String privilegeStr) {//为给定文件更改权限，filePath表示文件绝对路径；privilegeStr表示权限字符串，如："rwv--v--v"
        //返回值为真表示本次设置成功，假表示设置失败
        try {
            int pathINodeNum = getINodeNumberOfPath(filePath);
            int privilegeNum = FilePrivilege.stringToPrivilege(privilegeStr);

            iNodes.get(pathINodeNum).setPrivilege(currentUser, userManagement, privilegeNum);
            if (iNodes.get(pathINodeNum).getFileType() == FileTypeEnum.INODE_IS_DIRECTORY)
                for (String key : iNodes.get(pathINodeNum).getPathMap().keySet())
                    changePrivilege(filePath + "/" + key, privilegeStr);
            return true;
        } catch (OSException e) {
            e.printExceptionMessage();
            return false;
        }
    }

    public boolean checkPath(String filePath) {//检查文件路径是否存在
        String[] pathArray = filePath.split("/");

        int pathINodeNum = 0;//从根结点的INode编号开始向下寻找
        int index;

        for (int i = 1; i < pathArray.length; i++) {
            index = getIndexFromINodeNum(pathINodeNum);
            if (iNodes.get(index).getPathMap().containsKey(pathArray[i]))
                pathINodeNum = iNodes.get(index).getPathMap().get(pathArray[i]);//定位当前路径所指示的INode的编号
            else
                return false;
        }
        return true;
    }

    public void saveCurrentFileSystem() {
        try {
            JSONSaver.save(externalStorage, iNodes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //FileSystem fileSystem = new FileSystem();
        String currentPath = "~";
//        if (fileSystem.newFile(currentPath, "haha"))
//            fileSystem.writeFile(currentPath, "haha", "this is haha's content");
//
//        if (fileSystem.newFile(currentPath, "heihei"))
//            fileSystem.writeFile(currentPath, "heihei", "this is heihei's content");
//
//        if (fileSystem.newDirectory(currentPath, "directory"))
//            fileSystem.newFile(currentPath + "/directory", "haha in dir");
//
//        currentPath ="~/directory";
//        fileSystem.newFile(currentPath, "file1");
//        fileSystem.newDirectory(currentPath, "inDirectory");
//        currentPath = "~/directory/inDirectory";
//        fileSystem.newFile(currentPath, "file2");
//        fileSystem.newDirectory(currentPath, "directory2");
//        currentPath ="~/directory2";
//        fileSystem.newFile(currentPath, "haha1");
//        fileSystem.newFile(currentPath, "haha2");
        //fileSystem.move("heihei",currentPath,currentPath+"/directory/inDirectory");
        //fileSystem.copy("directory2",currentPath,currentPath+"/directory/inDirectory");
        //fileSystem.move("heihei",currentPath,currentPath+"/directory");
        //fileSystem.remove(currentPath, "directory");
        //System.out.println(fileSystem.allocatedINodeNum);
//        fileSystem.remove("~/","aaaa");
//        for (INode i : fileSystem.iNodes) {
//            System.out.println(i.getiNumber() + ": " + i.getFileName() + "  ---  " +  i.getFileType());
//        }
//        fileSystem.saveCurrentFileSystem();
        System.out.println("~".split("/").length);
    }
}
