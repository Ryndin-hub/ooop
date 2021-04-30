package commands;

import logic.Game;
import org.apache.log4j.Logger;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.IOException;

/**
 * Creates class one time for multiple use
 */
public class Factory {
    private final Logger log = Logger.getLogger(Factory.class);

    private final Map<String, String> commands;
    private final Map<String, Command> instances;

    private final Game game;

    /**
     * Initializes factory, to know there to find java classes reads commands.properties
     */
    public Factory(Game _game) throws IOException{
        game = _game;
        InputStream stream = ClassLoader.getSystemResourceAsStream("commands.properties");
        Properties properties = new Properties();
        properties.load(stream);
        stream.close();
        commands = new HashMap<>();
        instances = new HashMap<>();
        for (String cmd : properties.stringPropertyNames()) {
            commands.put(cmd, properties.getProperty(cmd));
        }
    }

    /**
     * First checks if factory know commandName, then checks if it already was create if not, creates it
     */
    public Command getCommand(String commandName) throws ClassNotFoundException {
        if (!commands.containsKey(commandName)) {
            log.error("No command matched name " + commandName);
            System.out.println("No command matched name " + commandName);
            return null;
        }
        if (instances.containsKey(commandName)) return instances.get(commandName);

        Command instance;
        try {
            instance = (Command) Class.forName(commands.get(commandName)).getConstructor(Game.class).newInstance(game);
            instances.put(commandName, instance);
        }
        catch (Exception e) {
            log.error("Error while creating instance of class" + commandName);
            throw new ClassNotFoundException(e.getLocalizedMessage());
        }
        return instance;
    }
}
