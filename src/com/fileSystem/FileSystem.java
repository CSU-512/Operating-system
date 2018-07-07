package com.fileSystem;

import com.exception.ExternalStorageSizeException;
import com.externalStorage.ExternalStorage;
import com.externalStorage.ExternalStorageInterface;
import com.internalStorage.InternalStorage;
import com.internalStorage.InternalStorageInterface;

import java.util.ArrayList;

public class FileSystem {//文件系统
    private ExternalStorageInterface externalStorage;//调用磁盘操作的接口
    private InternalStorageInterface internalStorage;//调用内存操作的接口
    private ArrayList<INode> iNodes;//iNode列表

    public FileSystem() {
        try {
            externalStorage = new ExternalStorage(2048);
            internalStorage = new InternalStorage();
            iNodes = new ArrayList<>();
        } catch (ExternalStorageSizeException e) {
            e.printExceptionMessage();
        }
    }


}
