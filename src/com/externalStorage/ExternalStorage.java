package com.externalStorage;

import com.exception.ExceptionEnum;
import com.exception.ExternalStorageOutOfStorageException;
import com.exception.ExternalStorageSizeException;

import java.util.ArrayList;

public class ExternalStorage{
    int size;				    // 磁盘空间
    int inUse;			        // 已用空间
    int blockSize;		        // 盘块大小
    boolean[] bitDiagram;	    // 位示图，false为空闲；true为占用

    // 带外存大小、已用空间、盘块大小的构造方法
    ExternalStorage(int size, int inUse, int blockSize) throws ExternalStorageSizeException{
        if(size % blockSize != 0)
            throw new ExternalStorageSizeException(ExceptionEnum.OS_EXTERNAL_STORAGE_SIZE_EXCEPTION);
        this.size = size;
        this.inUse = inUse;
        this.blockSize = blockSize;
        this.bitDiagram = new boolean[(size + blockSize - 1)/blockSize]; // 向上取整
        for(int i = 0; i < this.bitDiagram.length; i++)
            this.bitDiagram[i] = (i < inUse);
    }

    // 只规定外存大小的构造方法，其初态全为空
    ExternalStorage(int size) throws ExternalStorageSizeException{
        if(size % 4 != 0)
            throw new ExternalStorageSizeException(ExceptionEnum.OS_EXTERNAL_STORAGE_SIZE_EXCEPTION);
        this.size = size;
        this.inUse = 0;
        this.blockSize = 4;     // 默认盘块大小4kB
        this.bitDiagram = new boolean[(size + 3) / 4];                   // 向上取整
        for(int i = 0; i < this.bitDiagram.length; i++)
            this.bitDiagram[i] = false;
    }

    // 为文件分配盘块，分配的结果存放在参数中
    void salloc(int requiredSize, ArrayList<Integer> returnBlock) throws ExternalStorageOutOfStorageException {
        if(this.size - this.inUse < requiredSize)
            throw new ExternalStorageOutOfStorageException(ExceptionEnum.OS_EXTERNAL_STORAGE_OUT_OF_STORAGE_EXCEPTION);
        for(int i = 0; i < this.bitDiagram.length; i++){
            if(!this.bitDiagram[i]){
                returnBlock.add(i);
                this.bitDiagram[i] = true;
            }
        }
    }
    void sfree(ArrayList<Integer> usingBlock) {        // 释放参数所指定的盘块
        for(Integer i : usingBlock)
            this.bitDiagram[i] = false;
    }
}
