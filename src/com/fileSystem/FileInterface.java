package com.fileSystem;

import javafx.util.Pair;

import java.util.List;

public interface FileInterface {
    boolean newFile(String fileName);//新建文件，返回值表示文件创建是否成功；真为成功，假为失败
    boolean newDirectory(String directoryName);//新建目录，返回值表示目录创建是否成功；真为成功，假为失败
    String readFile(String fileName);//读文件，返回值为以字符串表示的文件内容
    boolean writeFile(String content);//写文件，参数为本次写操作结束后的文件内容，返回值表示本次写操作是否成功；真为成功，假为失败
    boolean copy(String sourceFileName, String targetPath);//文件复制，把源文件复制到目标路径，返回真表示成功，返回假表示失败
    void move(String sourceFileName, String targetPath);//文件移动，把源文件移动到目标路径
    void removeFile(String fileName);//删除文件
    void removeDirectory(String directoryName);//删除目录
    List<Pair<String, FileTypeEnum>> showDirectory(String path);//显示目录内容，参数为路径，返回值为列表；Pair中String参数表示文件或目录名，FileTypeEnum参数标识文件类型
}
