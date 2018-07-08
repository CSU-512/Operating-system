package com.util;

import com.exception.ExternalStorageOutOfStorageException;
import com.exception.ExternalStorageSizeException;
import com.externalStorage.ExternalStorage;
import com.fileSystem.INode;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class JSONSaver {

    public static void save(ExternalStorage externalStorage) {
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

    public static void main(String[] args) throws ExternalStorageSizeException, IOException, ExternalStorageOutOfStorageException {
        int diskSize = 20480;
        int diskInUse = 0;
        int diskBlockSize = 4;
        boolean[] bitDiagram = new boolean[diskSize / diskBlockSize];
        byte[][] data = new byte[diskSize / diskBlockSize][diskBlockSize * 1024];

        for (int i = 0; i < bitDiagram.length; i++)
            bitDiagram[i] = false;

//        testSerializable ts = new testSerializable("ha哈");
//        System.out.println(ts);
//        // serializable
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(baos);
//        oos.writeObject(ts);
//        byte[] b = baos.toByteArray();
//        JSONObject jo = new JSONObject();
//        jo.put("name", b);
//        System.out.println(jo);
//        oos.close();
//        baos.close();

        INode testNode = new INode();
        testNode.setAtime(new Date());

        for (int i = 0; i < data.length; i++)
            data[i] = "蛤蛤".getBytes("utf-8");

        ExternalStorage exs = new ExternalStorage(diskSize, diskInUse, diskBlockSize, bitDiagram, data);
        ArrayList<Integer> list = new ArrayList<>();
        exs.salloc("蛤蛤".getBytes("utf-8").length, list);
        exs.putData("蛤蛤".getBytes("utf-8"), list);

        byte[] test = exs.getData(list);
        System.out.println(new String(test, "utf-8") + " " + test.length);

        JSONSaver.save(exs);
    }
}
