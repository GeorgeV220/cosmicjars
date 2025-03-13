package com.georgev22.cosmicjars.command;

import com.georgev22.cosmicjars.CosmicJars;

public abstract class BaseCommand {

    private final String name;
    private final String description;
    protected final CosmicJars main = CosmicJars.getInstance();

    public BaseCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Executes the command
     *
     * @param args Command arguments
     */
    public abstract void execute(String[] args);
}
