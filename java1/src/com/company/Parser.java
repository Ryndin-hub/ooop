package com.company;

import java.util.Scanner;

public class Parser {
    Scanner in = new Scanner(System.in);

    public void parse_command(){
        String command = in.nextLine();
        String [] commands = command.split(" ");
        if (commands[0].equals("code")){
            Coder coder = new Coder(commands[1]);
            coder.code();
        }
        if (commands[0].equals("decode")){
            Decoder decoder = new Decoder(commands[1]);
            decoder.decode();
        }
    }
}
