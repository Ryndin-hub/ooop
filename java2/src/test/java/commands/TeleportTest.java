package commands;

import logic.Game;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TeleportTest {

    @Test
    public void execute() throws IOException {
        Game game = new Game();
        game.init(10,10,5,5);
        Teleport teleport = new Teleport(game);
        assertFalse(teleport.execute(new String[]{"0" , "0"}));
        assertTrue(teleport.execute(new String[]{"10" , "10"}));
        assertTrue(teleport.execute(new String[]{"-1" , "-1"}));
        assertTrue(teleport.execute(new String[]{"1000" , "1000"}));
        assertTrue(teleport.execute(new String[]{"10"}));
    }
}