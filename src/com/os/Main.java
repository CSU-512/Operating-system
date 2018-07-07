package com.os;

import com.externalStorage.ExternalStorage;
import javafx.util.Pair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Encoder;

import java.util.Comparator;


public class Main {

    public static void main(String[] args) throws UnsupportedEncodingException {
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
        String str = "haha";
        System.out.println(Base64.encodeBase64String(str.getBytes("utf-8")));
        System.out.println(new String(Base64.decodeBase64(Base64.encodeBase64String(str.getBytes("utf-8")))));
        System.out.println(new String(Base64.decodeBase64("aGFoYQ==")));
    }
}
