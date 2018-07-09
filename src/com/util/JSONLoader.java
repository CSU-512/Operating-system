package com.util;


import com.exception.ExternalStorageSizeException;
import com.externalStorage.ExternalStorage;
import com.fileSystem.INode;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

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
        String dataBase64;
        for (int i = 0; i < dataArray.length(); i++) {
            dataBase64 = dataArray.getString(i);
            data[i] = Base64.decodeBase64(dataBase64);
        }

        return new ExternalStorage(diskSize, diskInUse, diskBlockSize, bitDiagram, data);
    }

    //从字节数组中得到INode对象
    private static INode deSerializableINodeBytes(byte[] iNodeBytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(iNodeBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (INode) ois.readObject();
    }

    //利用本地JSON中的“INodeList”项构建磁盘对象
    public static ArrayList<INode> getINodeArray() throws IOException, ClassNotFoundException {
        JSONArray iNodeList = jsonObject.getJSONArray("INodeList");
        ArrayList<INode> iNodes = new ArrayList<>();
        String iNodeByteArrayBase64Str;

        for (int i = 0; i < iNodeList.length(); i++) {
            iNodeByteArrayBase64Str = iNodeList.getString(i);
            iNodes.add(deSerializableINodeBytes(Base64.decodeBase64(iNodeByteArrayBase64Str)));
        }

        return iNodes;
    }

    public static void main(String[] args) throws ExternalStorageSizeException, UnsupportedEncodingException {
    }
}
