package com.georgev22.cosmicjars.command.commands;

import com.georgev22.cosmicjars.command.BaseCommand;

public class ExitCommand extends BaseCommand {

    public ExitCommand() {
        super("exit", "Exits the program");
    }

    @Override
    public void execute(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        runtime.exit(0);
    }

}
