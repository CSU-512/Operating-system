package com.util;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DiskLoader {
    JSONObject diskInfo;

    public DiskLoader() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/com/util/Disk.json"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");
            JSONObject jsonObject = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
