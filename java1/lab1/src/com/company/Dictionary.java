package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    Map<String, String> code = new HashMap<>();
    Map<String, String> decode = new HashMap<>();

    Dictionary(String file){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null){
                String [] words = line.split(" ");
                code.put(words[0],words[1]);
                decode.put(words[1],words[0]);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
