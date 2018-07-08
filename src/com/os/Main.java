package com.os;

import com.exception.OSException;
import com.externalStorage.ExternalStorage;
import com.userManagement.UserManagement;
import com.userManagement.UserTypeEnum;
import javafx.util.Pair;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;
import sun.misc.BASE64Encoder;

import java.util.Comparator;


public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, OSException {
	// write your code here
        System.out.println("haha");
        ArrayList<Pair<Integer, Integer>> arl = new ArrayList<>();
        arl.add(new Pair<>(3,2));
        arl.add(new Pair<>(29,1));
        arl.add(new Pair<>(0,23));
        Comparator<Pair<Integer, Integer>> c = new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                return o1.getKey() - o2.getKey();
            }
        };
        arl.sort(c);
        for(Pair<Integer, Integer> p : arl)
            System.out.print(p.getKey() + " ");
        System.out.println();
        String str = "蛤蛤";
        System.out.println(Base64.encodeBase64String(str.getBytes("utf-8")));
        System.out.println(new String(Base64.decodeBase64(Base64.encodeBase64String(str.getBytes("utf-8")))));
        System.out.println(new String(Base64.decodeBase64("aGFoYQ==")));

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
//        // deserializable
//        ByteArrayInputStream bais = new ByteArrayInputStream(b);
//        ObjectInputStream ois = new ObjectInputStream(bais);
//        testSerializable tsi = (testSerializable)ois.readObject();
//        System.out.println(tsi);

//        UserManagement um = new UserManagement();
//        um.createNewUser("root", "password", 0, UserTypeEnum.OS_SUPERUSER);
//        ByteArrayOutputStream bo = new ByteArrayOutputStream();
//        ObjectOutputStream os = new ObjectOutputStream(bo);
//        os.writeObject(um);
//        byte[] userByte = bo.toByteArray();
//        bo.close();
//        os.close();
//
//        JSONObject jo = new JSONObject();
//        jo.put("UserManagement", Base64.encodeBase64String(userByte));
//        FileOutputStream fos = new FileOutputStream(new File("UserManagement.json"));
//        fos.write(jo.toString().getBytes());
//        fos.close();


        FileInputStream fis = new FileInputStream(new File("UserManagement.json"));
        byte[] userJSONByte = new byte[(int) new File("UserManagement.json").length()];
        fis.read(userJSONByte);
        JSONObject jo = new JSONObject(new String(userJSONByte));
        System.out.println(jo);
        byte[] userByte = Base64.decodeBase64(jo.getString("UserManagement"));
        ByteArrayInputStream bis = new ByteArrayInputStream(userByte);
        ObjectInputStream objectInputStream = new ObjectInputStream(bis);
        UserManagement um = (UserManagement) objectInputStream.readObject();
        System.out.println(um.findUser(0).getUserName()+"\n"+um.findUser(0).getUserType().getUserType());
        System.out.println(um.findUser(0).getPassword());
    }
}

class testSerializable implements Serializable{
    private String name;
    testSerializable(String name){
        this.name = name;
    }
    public String toString(){
        return name;
    }
}