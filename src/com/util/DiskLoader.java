package com.util;


import com.exception.ExternalStorageSizeException;
import com.externalStorage.ExternalStorage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class DiskLoader {
    private JSONObject diskInfo;

    public DiskLoader() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/com/util/Disk.json"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");
            diskInfo = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ExternalStorage getExternalStorageFromJson() throws ExternalStorageSizeException, UnsupportedEncodingException {
        int diskSize = diskInfo.getInt("diskSize");
        int diskInUse = diskInfo.getInt("diskInUse");
        int diskBlockSize = diskInfo.getInt("diskBlockSize");
        boolean[] bitDiagram = new boolean[diskSize / diskBlockSize];
        byte[][] data = new byte[diskSize / diskBlockSize][diskBlockSize];

        JSONArray bitDiagramJSONArray = diskInfo.getJSONArray("bitDiagram");
        for (int i = 0; i < bitDiagramJSONArray.length() / diskBlockSize; i++) {
            bitDiagram[i] = bitDiagramJSONArray.getBoolean(i);
        }

        JSONArray dataJSONArray = diskInfo.getJSONArray("data");
        for (int i = 0; i < 1; i++)
            for (int j = 0; j < dataJSONArray.getJSONArray(i).length(); j++) {
                System.out.println(dataJSONArray.getJSONArray(i).length());
                data[i][j] = (byte) dataJSONArray.getJSONArray(i).getInt(i);
            }
        System.out.println(new String(data[0],"utf-8"));

        return new ExternalStorage(diskSize, diskInUse, diskBlockSize, bitDiagram, data);
    }

    public static void main(String[] args) throws ExternalStorageSizeException, UnsupportedEncodingException {
        ExternalStorage exs = new DiskLoader().getExternalStorageFromJson();

        System.out.println(exs.getData().toString());
    }
}
