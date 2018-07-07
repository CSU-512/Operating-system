package com.internalStorage;

import com.exception.InternalStorageOutOfStorageException;
import javafx.util.Pair;

public interface InternalStorageInterface {
    int isalloc(int size) throws InternalStorageOutOfStorageException;

    void isfree(Pair<Integer, Integer> startAndLength);
}
