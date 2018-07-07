package com.internalStorage;

import com.exception.ExceptionEnum;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;

import com.exception.InternalStorageOutOfStorageException;

public class InternalStorage implements InternalStorageInterface {

    private int size;           // 外存总容量(byte)
    private int inUse;          // 已占用的外存容量(byte)
    private Pair<Integer, Integer> startAndLength;  // 空闲内存始址和偏移量
    private ArrayList<Pair<Integer, Integer>> availableISTable;

    // 初始化空闲内存表，空闲始址0， 长度size
    InternalStorage(int size) {
        this.inUse = 0;
        this.availableISTable = new ArrayList<Pair<Integer, Integer>>();
        this.availableISTable.add(new Pair<>(0, size));
    }

    // 分配内存空间，首次适应算法
    public int isalloc(int size) throws InternalStorageOutOfStorageException {

        // 每次释放主存空间后，系统确保进行紧凑操作，故只需判断总的主存剩余容量是否满足需求即可
        // 若主存容量不足，则跑出主存容量不足异常
        if (this.size - inUse < size)
            throw new InternalStorageOutOfStorageException(ExceptionEnum.OS_INTERNAL_STORAGE_OUT_OF_STORAGE_EXCEPTION);
        int i;
        for (i = 0; i < this.availableISTable.size(); i++) {
            if (this.availableISTable.get(i).getValue() >= size)
                break;
        }
        if (i < this.availableISTable.size()) {
            Pair<Integer, Integer> p = this.availableISTable.get(i);
            this.availableISTable.remove(i);
            if (p.getValue() - size > 0)    // 如果一个空闲区间正好被占满，就无需再记录长度为0的空闲区间了
                this.availableISTable.add(new Pair<>(p.getKey() + size, p.getValue() - size));
            this.inUse += size;
            return p.getKey();
        }
        return -1;
    }

    // TODO: 2018/7/7 测试内存释放功能
    // 释放内存空间
    public void isfree(Pair<Integer, Integer> startAndLength) {

        // 按第一关键字升序比较的比较器
        Comparator<Pair<Integer, Integer>> c = new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                return o1.getKey() - o2.getKey();
            }
        };

        // 按第一关键字升序排序
        this.availableISTable.sort(c);
        int index = 0, sizeOfAvailableISTable = this.availableISTable.size();

        // 在当前主存区间之前，找到离当前主存区间最近的空闲主存区间
        while (index < sizeOfAvailableISTable &&
                this.availableISTable.get(index).getKey() + this.availableISTable.get(index).getValue()
                        < startAndLength.getValue() - 1)
            index++;

        // 如果当前主存区间之前最近的空闲主存区间与之相邻，则扩展这个空闲主存区间
        if (this.availableISTable.get(index).getKey() + this.availableISTable.get(index).getValue() ==
                startAndLength.getKey()) {
            Integer key = this.availableISTable.get(index).getKey();
            Integer value = this.availableISTable.get(index).getValue();
            this.availableISTable.set(index, new Pair<>(key, value + startAndLength.getValue()));
        } else {
            this.availableISTable.add(startAndLength);
            index++;
            sizeOfAvailableISTable++;
        }

        // 确保接下来的操作继续以第一关键字升序排列为基础
        this.availableISTable.sort(c);
        if (index < sizeOfAvailableISTable)
            index++;

        // 如果当前主存区间之后相邻的的主存是空闲的，则删除它，并扩展上一个空闲区间
        if (startAndLength.getKey() + startAndLength.getValue() == this.availableISTable.get(index).getKey()) {
            Integer key = this.availableISTable.get(index - 1).getKey();
            Integer value = this.availableISTable.get(index - 1).getValue();
            this.availableISTable.set(
                    index - 1,
                    new Pair<>(key, value + this.availableISTable.get(index).getValue())
            );
            this.availableISTable.remove(index);
        }
        this.size -= startAndLength.getValue();
    }
}
