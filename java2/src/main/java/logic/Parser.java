package logic;
import commands.Factory;
import commands.Command;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;

/**
 * Parses user input
 */
public class Parser {
    private final Scanner in = new Scanner(System.in);
    private final Logger log = Logger.getLogger(Parser.class);
    private Factory factory;
    private Game game;

    /**
     * Connects parser to given game and creates new factory
     * @see Factory
     */
    public Parser(Game _game) throws IOException {
        game = _game;
        factory = new Factory(_game);
    }

    /**
     * Reads input from console, splits it, asks factory for command and executes it
     * @see Factory#getCommand(String)
     * @see Command
     */
    public void parse_command() throws ClassNotFoundException {
        String input = in.nextLine();
        String[] commands = input.split(" ");
        if (commands.length < 1 || commands[0].length() < 1){
            System.out.println("No command given");
            log.error("No command given");
            return;
        }
        Command command = factory.getCommand(commands[0]);
        if (command != null) {
            command.execute(Arrays.copyOfRange(commands, 1, commands.length));
        }
    }
}
