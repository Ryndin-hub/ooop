package commands;
import logic.Game;
import org.apache.log4j.Logger;

public class Draw implements Command {
    private final Game game;
    private final Logger log = Logger.getLogger(Draw.class);
    public Draw(Game _game){
        game = _game;
        log.info("Draw command created");
    }
    public boolean execute(String[] args){
        game.turtle.setDrawState(true);
        log.info("Turtle is now drawing");
        return false;
    }
}
