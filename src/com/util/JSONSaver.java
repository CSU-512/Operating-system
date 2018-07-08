package com.util;

import com.exception.ExternalStorageOutOfStorageException;
import com.exception.ExternalStorageSizeException;
import com.externalStorage.ExternalStorage;
import com.fileSystem.FileTypeEnum;
import com.fileSystem.INode;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class JSONSaver {

    public static void save(ExternalStorage externalStorage, ArrayList<INode> iNodes) throws IOException {
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

        //把INode列表装入JSON
        JSONArray inodeListInfo = new JSONArray();
        for (INode iNode : iNodes) {
            inodeListInfo.put(Base64.encodeBase64String(getSerializableINode(iNode)));
        }
        jsonObject.put("INodeList",inodeListInfo);

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

    //序列化iNode，得到字节数组
    public static byte[] getSerializableINode(INode iNode) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(iNode);
        return baos.toByteArray();
    }

    //初始化JSON文件
    private static void init() throws ExternalStorageSizeException, IOException {
        //构建磁盘信息
        int diskSize = 20480;//磁盘总容量为2MB
        int diskInUse = 0;//初始使用盘块为0
        int diskBlockSize = 4;//盘块大小为4KB
        boolean[] bitDiagram = new boolean[diskSize / diskBlockSize];
        byte[][] data = new byte[diskSize / diskBlockSize][diskBlockSize * 1024];

        for (int i = 0; i < bitDiagram.length; i++)
            bitDiagram[i] = false;//初始化位示图
        ExternalStorage exs = new ExternalStorage(diskSize, diskInUse, diskBlockSize, bitDiagram, data);

        //构建初始INode，即根结点信息
        ArrayList<INode> iNodes = new ArrayList<>();
        iNodes.add(new INode(0, -1, "~", 0, FileTypeEnum.INODE_IS_DIRECTORY, "user1", "group1",
                            0, new Date(), new Date(), new Date(), 0,
                            new ArrayList<>(), new HashMap<>()));//加入一个根结点

        //存入JSON文件
        JSONSaver.save(exs, iNodes);
    }

    public static void main(String[] args) throws ExternalStorageSizeException, IOException {
        init();
    }
}
