package commands;
import logic.Game;
import org.apache.log4j.Logger;

public class Teleport implements Command {
    private final Game game;
    private final Logger log = Logger.getLogger(Teleport.class);
    public Teleport(Game _game){
        game = _game;
        log.info("Teleport command created");
    }
    public boolean execute(String[] args){
        if (args.length < 2){
            System.out.println("Too few arguments");
            log.error("Too few arguments");
            return true;
        }
        if (game.turtle.teleport(Integer.parseInt(args[0]),Integer.parseInt(args[1]))){
            System.out.println("Bad arguments");
            log.error("Bad arguments");
            return true;
        }
        log.info("Turtle teleported to " + args[0] + " " + args[1]);
        return false;
    }
}
