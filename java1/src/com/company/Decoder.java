package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Decoder {
    String input;
    Dictionary dictionary = new Dictionary("RU.txt");
    Set<CharCounter> statistics = new HashSet<>();

    public Decoder(String _input) {
        input = _input + ".txt";
    }

    public void decode(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            PrintStream printStream = new PrintStream("output.txt");
            String line = reader.readLine();
            while (line != null){
                if (!line.equals("")) {
                    String[] words = line.split("    ");
                    for (String word : words) {
                        String[] characters = word.split(" ");
                        for (String character : characters) {
                            printStream.print(dictionary.decode.get(character));
                            statistics.add(new CharCounter(dictionary.decode.get(character)));
                        }
                        printStream.print(' ');
                    }
                }
                printStream.println();
                line = reader.readLine();
            }
            List<CharCounter> sortedStatistics = statistics.stream().sorted().collect(Collectors.toList());
            for(CharCounter character: sortedStatistics){
                printStream.println(character.character + " " + character.counter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
