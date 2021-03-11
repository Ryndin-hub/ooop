package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.List;

public class Coder {
    String input;
    Dictionary dictionary = new Dictionary("RU.txt");
    Set<CharCounter> statistics = new HashSet<>();

    public Coder(String _input) {
        input = _input + ".txt";
    }

    public void code(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            PrintStream printStream = new PrintStream("output.txt");
            String line = reader.readLine();
            while (line != null){
                line = line.toUpperCase();
                if (!line.equals("")) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        String[] characters = word.split("");
                        for (String character : characters) {
                            if (character.length() == 0) continue;
                            char c = character.charAt(0);
                            printStream.print(dictionary.code.get(c));
                            statistics.add(new CharCounter(c));
                            printStream.print(' ');
                        }
                        printStream.print("   ");
                    }
                }
                printStream.println();
                line = reader.readLine();
            }
            printStream = new PrintStream("stats.txt");
            List<CharCounter> sortedStatistics = statistics.stream().sorted().collect(Collectors.toList());
            for(CharCounter character: sortedStatistics){
                printStream.println(character.character + " " + character.counter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
