package commands;

/**
 * Parent class for all commands
 */
public interface Command {
    boolean execute(String[] args);
}
