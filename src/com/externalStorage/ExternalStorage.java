package com.externalStorage;

import com.exception.ExceptionEnum;
import com.exception.ExternalStorageOutOfStorageException;
import com.exception.ExternalStorageSizeException;

import java.util.ArrayList;

public class ExternalStorage {
    private int size;                       // 磁盘空间(kbyte)
    private int inUse;                      // 已用空间(kbyte)
    private int blockSize;                  // 盘块大小(kbyte)
    private boolean[] bitDiagram;           // 位示图，false为空闲；true为占用
    private byte[][] data;

    public ExternalStorage(int size, int inUse, int blockSize, boolean[] bitDiagram, byte[][] data)
            throws ExternalStorageSizeException {
        if (size % 4 != 0)
            throw new ExternalStorageSizeException(ExceptionEnum.OS_EXTERNAL_STORAGE_SIZE_EXCEPTION);
        this.size = size;
        this.inUse = inUse;
        this.blockSize = blockSize;
        this.bitDiagram = bitDiagram;
        this.data = data;
    }

    public int getSize() {
        return size;
    }

    public int getInUse() {
        return inUse;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public boolean[] getBitDiagram() {
        return bitDiagram;
    }

    public byte[][] getData() {
        return data;
    }

    /**
     *
     * @param requiredSize  (byte)需要的字节数
     * @param returnBlock   将分配的盘块号存入此数组中
     * @throws ExternalStorageOutOfStorageException 外存空间不足时抛出
     * @apiNote 为文件分配盘块，分配的结果存放在returnBlock中
     */
    public void salloc(int requiredSize, ArrayList<Integer> returnBlock) throws ExternalStorageOutOfStorageException {
        if (this.size - this.inUse < requiredSize)
            throw new ExternalStorageOutOfStorageException(ExceptionEnum.OS_EXTERNAL_STORAGE_OUT_OF_STORAGE_EXCEPTION);
        int k = 0;  // 分配的盘块数
        for (int i = 0; i < this.bitDiagram.length && k*(blockSize*1024) < requiredSize; i++) {
            if (!this.bitDiagram[i]) {
                returnBlock.add(i);
                k++;
                this.bitDiagram[i] = true;
            }
        }
        inUse += k*blockSize;
    }

    // 将原数据和已分配的盘块传入该方法，将数据转为字节型后离散地存储
    public void putData(byte[] rawByteData, ArrayList<Integer> allocatedBlock) {
//        byte[] rawByteData = rawData.getBytes();
        int k = 0, i;
        byte[][] separatedByteData = new byte[allocatedBlock.size()][];
        // 为整除盘块长度的部分分配完整盘块
        for (i = 0; i < rawByteData.length / 1024 / blockSize; i++) {
            separatedByteData[i] = new byte[1024*blockSize];
            for (int j = 0; j < this.blockSize * 1024; j++) {
                separatedByteData[i][j] = rawByteData[k++];
            }
        }
        // 为除盘块长度得到的余数部分分配部分盘块，剩余容量成为内碎片
        if(rawByteData.length % (1024*blockSize) > 0){
            separatedByteData[i] = new byte[rawByteData.length % (1024*blockSize)];
            for(int j = 0; j < rawByteData.length % (1024*blockSize); j++)
                separatedByteData[i][j] = rawByteData[k++];
        }
        k = 0;
        for (Integer index : allocatedBlock) {            // 将字节型数据挨个放入磁盘中
            data[index] = separatedByteData[k++];
        }
    }

    // 将离散存储的数据拼接起来返回给调用者
    public byte[] getData(ArrayList<Integer> allocatedBlock) {
        byte[][] separatedByteData = new byte[allocatedBlock.size()][/*this.blockSize * 1024*/];
        int j = 0, k = 0, fileLength = 0;
        for (int i : allocatedBlock) {            // 将离散数据联系起来
            separatedByteData[j++] = data[i];
            fileLength += data[i].length;
        }

//        byte[] rawByteData = new byte[allocatedBlock.size() * this.blockSize * 1024];
        byte[] rawByteData = new byte[fileLength];
        for (byte[] i : separatedByteData)        // 将离散数据连接起来
            for (byte l : i)
                rawByteData[k++] = l;
//        return new String(rawByteData);           // 重新构造字符串
        return rawByteData;
    }

    // 释放参数所指定的盘块
    public void sfree(ArrayList<Integer> usingBlock) {
        for (Integer i : usingBlock)
            this.bitDiagram[i] = false;
        inUse -= usingBlock.size();
    }

        /*
    // 带外存大小、已用空间、盘块大小的构造方法
    ExternalStorage(int size, int inUse, int blockSize) throws ExternalStorageSizeException {
        if (size % blockSize != 0)
            throw new ExternalStorageSizeException(ExceptionEnum.OS_EXTERNAL_STORAGE_SIZE_EXCEPTION);
        this.size = size;
        this.inUse = inUse;
        this.blockSize = blockSize;
        this.bitDiagram = new boolean[(size + blockSize - 1) / blockSize]; // 向上取整
        this.data = new byte[(size + blockSize - 1) / blockSize][blockSize * 1024];
        for (int i = 0; i < this.bitDiagram.length; i++)
            this.bitDiagram[i] = (i < inUse);
    }

    // 只规定外存大小的构造方法，其初态全为空
    ExternalStorage(int size) throws ExternalStorageSizeException {
        if (size % 4 != 0)
            throw new ExternalStorageSizeException(ExceptionEnum.OS_EXTERNAL_STORAGE_SIZE_EXCEPTION);
        this.size = size;
        this.inUse = 0;
        this.blockSize = 4;                         // 默认盘块大小4kB
        this.bitDiagram = new boolean[(size + 3) / 4];     // 向上取整
        this.data = new byte[(size + 3) / 4][this.blockSize * 1024];
        for (int i = 0; i < this.bitDiagram.length; i++)
            this.bitDiagram[i] = false;
    }
    */
}
