package com.util;

import com.exception.ExternalStorageSizeException;
import com.externalStorage.ExternalStorage;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class DiskSaver {
    private ExternalStorage externalStorage;

    public DiskSaver(ExternalStorage externalStorage) {
        this.externalStorage = externalStorage;
    }

    public void save() {
        JSONObject diskInfo = new JSONObject();
        diskInfo.put("diskSize", externalStorage.getSize());
        diskInfo.put("diskInUse", externalStorage.getInUse());
        diskInfo.put("diskBlockSize", externalStorage.getBlockSize());
        diskInfo.put("bitDiagram", externalStorage.getBitDiagram());
        byte[][] data = externalStorage.getData();
        JSONArray dataArray = new JSONArray();
        for (int i = 0; i < data.length; i++)
            dataArray.put(Base64.encodeBase64String(data[i]));
        diskInfo.put("data", dataArray);
        File file = new File("src/com/util/Disk.json");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            out.write(diskInfo.toString().getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ExternalStorageSizeException, UnsupportedEncodingException {
        int diskSize = 2048;
        int diskInUse = 0;
        int diskBlockSize = 4;
        boolean[] bitDiagram = new boolean[diskSize / diskBlockSize];
        byte[][] data = new byte[diskSize / diskBlockSize][diskBlockSize];

        for (int i = 0; i < bitDiagram.length; i++)
            bitDiagram[i] = true;


        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[i].length; j++)
                data[i][j] = "蛤".getBytes("GBK")[j%2];

        ExternalStorage exs = new ExternalStorage(diskSize, diskInUse, diskBlockSize, bitDiagram, data);

        new DiskSaver(exs).save();
    }
}
