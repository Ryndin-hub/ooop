package commands;

import logic.Game;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class MoveTest {

    @Test
    public void execute() throws IOException {
        Game game = new Game();
        game.init(10,10,5,5);
        Move move = new Move(game);
        assertFalse(move.execute(new String[]{"D" , "10"}));
        assertTrue(move.execute(new String[]{"X" , "10"}));
        assertTrue(move.execute(new String[]{"R" , "-1"}));
        assertTrue(move.execute(new String[]{"L" , "0"}));
    }
}