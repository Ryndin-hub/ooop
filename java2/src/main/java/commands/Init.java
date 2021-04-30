package commands;

import logic.Game;
import org.apache.log4j.Logger;

public class Init implements Command {
    private final Game game;
    private final Logger log = Logger.getLogger(Init.class);
    public Init(Game _game){
        game = _game;
        log.info("Init command created");
    }
    public boolean execute(String[] args){
        if (args.length < 4){
            System.out.println("Too few arguments");
            log.error("Too few arguments");
            return true;
        }
        int width = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);
        int x = Integer.parseInt(args[2]);
        int y = Integer.parseInt(args[3]);
        if (width < 1 || height < 1 || x < 0 || y < 0 || x >= width || y >= height){
            System.out.println("Bad arguments");
            log.error("Bad arguments");
            return true;
        }
        game.init(Integer.parseInt(args[0]),Integer.parseInt(args[1]),
                Integer.parseInt(args[2]),Integer.parseInt(args[3]));
        return false;
    }
}
