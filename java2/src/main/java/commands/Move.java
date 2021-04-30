package commands;
import logic.Direction;
import logic.Game;
import org.apache.log4j.Logger;

public class Move implements Command {
    private final Game game;
    private final Logger log = Logger.getLogger(Move.class);
    public Move(Game _game){
        game = _game;
        log.info("Move command created");
    }
    public boolean execute(String[] args){
        if (args.length < 2){
            System.out.println("Too few arguments");
            log.error("Too few arguments");
            return true;
        }
        if (Integer.parseInt(args[1]) < 1){
            System.out.println("Bad arguments");
            log.error("Bad arguments");
            return true;
        }
        switch (args[0]){
            case "U": {
                game.turtle.move(Direction.U,Integer.parseInt(args[1]));
                log.info("Moved up " + Integer.parseInt(args[1]) + " steps");
                break;
            }
            case "D": {
                game.turtle.move(Direction.D,Integer.parseInt(args[1]));
                log.info("Moved down " + Integer.parseInt(args[1]) + " steps");
                break;
            }
            case "L": {
                game.turtle.move(Direction.L,Integer.parseInt(args[1]));
                log.info("Moved left " + Integer.parseInt(args[1]) + " steps");
                break;
            }
            case "R": {
                game.turtle.move(Direction.R,Integer.parseInt(args[1]));
                log.info("Moved right " + Integer.parseInt(args[1]) + " steps");
                break;
            }
            default:
                System.out.println("Wrong direction");
                log.error("Wrong direction");
                return true;
        }
        return false;
    }
}
