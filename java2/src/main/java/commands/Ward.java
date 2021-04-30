package commands;
import logic.Game;
import org.apache.log4j.Logger;

public class Ward implements Command {
    private final Game game;
    private final Logger log = Logger.getLogger(Ward.class);
    public Ward(Game _game){
        game = _game;
        log.info("Ward command created");
    }
    public boolean execute(String[] args){
        game.turtle.setDrawState(false);
        log.info("Turtle is no longer drawing");
        return false;
    }
}
