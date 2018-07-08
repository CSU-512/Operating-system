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

public class JSONSaver {
    private ExternalStorage externalStorage;

    public JSONSaver(ExternalStorage externalStorage) {
        this.externalStorage = externalStorage;
    }

    public void save() {
        JSONObject jsonObject = new JSONObject();

        //把磁盘相关信息装入JSON
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
        jsonObject.put("Disk", diskInfo);


        //将JSON内容写入文件
        File file = new File("src/com/util/Config.json");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            out.write(jsonObject.toString().getBytes());
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


        for (int i = 0; i < data.length; i++) {
            data[i] = "蛤蛤蛤".getBytes("utf-8");
        }

        ExternalStorage exs = new ExternalStorage(diskSize, diskInUse, diskBlockSize, bitDiagram, data);

        new JSONSaver(exs).save();
    }
}
