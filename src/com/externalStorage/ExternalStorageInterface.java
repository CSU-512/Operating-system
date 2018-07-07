package com.externalStorage;

import com.exception.ExternalStorageOutOfStorageException;

import java.util.ArrayList;

public interface ExternalStorageInterface {
    void salloc(int requiredSize, ArrayList<Integer> returnBlock) throws ExternalStorageOutOfStorageException;

    void putData(String rawData, ArrayList<Integer> allocatedBlock);

    void sfree(ArrayList<Integer> usingBlock);

    String getData(ArrayList<Integer> allocatedBlock);

}
