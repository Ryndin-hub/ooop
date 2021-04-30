package commands;
import logic.Game;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class InitTest {

    @Test
    public void execute() throws IOException {
        Game game = new Game();
        Init init = new Init(game);
        assertFalse(init.execute(new String[]{"10", "10", "5", "5"}));
        assertTrue(init.execute(new String[]{"10", "10", "10", "10"}));
        assertFalse(init.execute(new String[]{"10", "10", "0", "0"}));
        assertTrue(init.execute(new String[]{"10", "10", "-1", "-1"}));
        assertTrue(init.execute(new String[]{"10", "10", "5"}));
        assertTrue(init.execute(new String[]{"-1", "-1", "-1", "-1"}));
    }
}