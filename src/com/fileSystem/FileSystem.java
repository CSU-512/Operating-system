//package com.fileSystem;
//
//import com.exception.ExternalStorageSizeException;
//import com.externalStorage.ExternalStorage;
//import com.externalStorage.ExternalStorageInterface;
//import com.internalStorage.InternalStorage;
//import com.internalStorage.InternalStorageInterface;
//import javafx.util.Pair;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FileSystem implements FileInterface {//文件系统
//    private ExternalStorageInterface externalStorage;//调用磁盘操作的接口
//    private InternalStorageInterface internalStorage;//调用内存操作的接口
//    private List<INode> iNodes;//iNode列表
//
//    public FileSystem() {
//        try {
//            externalStorage = new ExternalStorage(2048);
//            internalStorage = new InternalStorage();
//            iNodes = new ArrayList<>();
//        } catch (ExternalStorageSizeException e) {
//            e.printExceptionMessage();
//        }
//    }
//
//    public boolean newFile(String currentPath, String fileName) {//新建文件，返回值表示文件创建是否成功；真为成功，假为失败
//
//    }
//    public boolean newDirectory(String currentPath, String directoryName) {//新建目录，返回值表示目录创建是否成功；真为成功，假为失败
//
//    }
//    public String readFile(String currentPath, String fileName) {//读文件，返回值为以字符串表示的文件内容
//
//    }
//    public boolean writeFile(String currentPath, String content) {//写文件，参数为本次写操作结束后的文件内容，返回值表示本次写操作是否成功；真为成功，假为失败
//
//    }
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
//    public List<Pair<String, FileTypeEnum>> showDirectory(String currentPath) {//显示目录内容，参数为路径，返回值为列表；Pair中String参数表示文件或目录名，FileTypeEnum参数标识文件类型
//
//    }
//}
