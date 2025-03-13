package com.georgev22.cosmicjars.command;

import com.georgev22.cosmicjars.CosmicJars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages a collection of commands, allowing registration, execution, and retrieval.
 */
public class CommandManager {

    /**
     * Mapping of command names to their corresponding command instances.
     */
    private final Map<String, BaseCommand> commandMap = new HashMap<>();

    private final CosmicJars main = CosmicJars.getInstance();

    /**
     * Registers a command with the manager.
     *
     * @param command the command to register
     */
    public void registerCommand(@NotNull BaseCommand command) {
        commandMap.put(command.getName(), command);
    }

    /**
     * Unregisters a command from the manager.
     *
     * @param name the name of the command to unregister
     */
    public void unregisterCommand(@NotNull String name) {
        commandMap.remove(name);
    }

    /**
     * Executes a command with the given name and arguments.
     *
     * @param name the name of the command to execute
     * @param args the arguments to pass to the command
     */
    public void executeCommand(@NotNull String name, @NotNull String @NotNull [] args) {
        BaseCommand command = commandMap.get(name.toLowerCase());
        if (command == null) {
            this.main.getLogger().info("Unknown command: {}, type 'help' for help", String.join(" ", name));
            return;
        }
        commandMap.get(name).execute(args);
    }

    /**
     * Checks if a command with the given name is registered.
     *
     * @param name the name of the command to check
     * @return true if the command is registered, false otherwise
     */
    public boolean hasCommand(@NotNull String name) {
        return commandMap.containsKey(name.toLowerCase());
    }

    /**
     * Retrieves a command instance by its name.
     *
     * @param name the name of the command to retrieve
     * @return the command instance, or null if not found
     */
    public @Nullable BaseCommand getCommand(@NotNull String name) {
        return commandMap.get(name);
    }

    /**
     * Returns a list of all registered commands.
     *
     * @return a list of registered commands
     */
    public @NotNull List<BaseCommand> getCommands() {
        return commandMap.values().stream().toList();
    }
}