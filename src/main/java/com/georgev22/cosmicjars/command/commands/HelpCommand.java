package com.georgev22.cosmicjars.command.commands;

import com.georgev22.cosmicjars.command.BaseCommand;

public class HelpCommand extends BaseCommand {

    public HelpCommand() {
        super("help", "Displays a list of available commands.");
    }


    @Override
    public void execute(String[] args) {
        for (BaseCommand command : this.main.getCommandManager().getCommands()) {
            this.main.getLogger().info("{}: {}", command.getName(), command.getDescription());
        }
    }
}
