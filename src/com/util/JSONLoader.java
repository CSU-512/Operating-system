package com.util;


import com.exception.ExternalStorageSizeException;
import com.externalStorage.ExternalStorage;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class JSONLoader {
    private static JSONObject jsonObject;

    static {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/com/util/Config.json"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");
            jsonObject = new JSONObject(sb.toString());//获得文件中保存的整个JSON
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //利用本地JSON中的“Disk”项构建磁盘对象
    public static ExternalStorage getExternalStorageFromJson() throws ExternalStorageSizeException {
        JSONObject diskInfo = jsonObject.getJSONObject("Disk");
        int diskSize = diskInfo.getInt("diskSize");
        int diskInUse = diskInfo.getInt("diskInUse");
        int diskBlockSize = diskInfo.getInt("diskBlockSize");
        boolean[] bitDiagram = new boolean[diskSize / diskBlockSize];
        byte[][] data = new byte[diskSize / diskBlockSize][diskBlockSize * 1024];

        JSONArray bitDiagramJSONArray = diskInfo.getJSONArray("bitDiagram");//构建位示图
        for (int i = 0; i < bitDiagramJSONArray.length() / diskBlockSize; i++) {
            bitDiagram[i] = bitDiagramJSONArray.getBoolean(i);
        }

        JSONArray dataArray = diskInfo.getJSONArray("data");//构建磁盘块内容
        String[] dataBase64 = new String[diskSize / diskBlockSize];
        for (int i = 0; i < dataArray.length(); i++) {
            dataBase64[i] = dataArray.getString(i);
            data[i] = Base64.decodeBase64(dataBase64[i]);
        }

        return new ExternalStorage(diskSize, diskInUse, diskBlockSize, bitDiagram, data);
    }

    public static void main(String[] args) throws ExternalStorageSizeException, UnsupportedEncodingException {
        ExternalStorage exs = JSONLoader.getExternalStorageFromJson();

        byte[][] bytes = exs.getData();
        for (int i = 0; i < bytes.length; i++)
            System.out.println(new String(bytes[i],"utf-8").length());
    }
}
