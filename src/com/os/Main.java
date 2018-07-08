package com.os;

import com.externalStorage.ExternalStorage;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;

import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import sun.misc.BASE64Encoder;

import java.util.Comparator;


public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
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

        testSerializable ts = new testSerializable("ha哈");
        System.out.println(ts);
        // serializable
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(ts);
        byte[] b = baos.toByteArray();
        JSONObject jo = new JSONObject();
        jo.put("name", b);
        System.out.println(jo);
        oos.close();
        baos.close();
        // deserializable
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        testSerializable tsi = (testSerializable)ois.readObject();
        System.out.println(tsi);
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