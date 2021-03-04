package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class Coder {
    String input;
    Dictionary dictionary = new Dictionary("RU.txt");

    Coder(String _input){
        input = _input + ".txt";
    }

    void code(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            PrintStream printStream = new PrintStream("output.txt");
            String line = reader.readLine();
            while (line != null){
                String [] words = line.split(" ");
                for (String word : words){
                    String [] characters = word.split("");
                    for (String character : characters){
                        printStream.print(dictionary.code.get(character));
                        printStream.print(' ');
                    }
                    printStream.print("   ");
                }
                printStream.println();
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void decode(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            PrintStream printStream = new PrintStream("output.txt");
            String line = reader.readLine();
            while (line != null){
                String [] words = line.split("    ");
                for (String word : words){
                    String [] characters = word.split(" ");
                    for (String character : characters){
                        printStream.print(dictionary.decode.get(character));
                    }
                    printStream.print(' ');
                }
                printStream.println();
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
