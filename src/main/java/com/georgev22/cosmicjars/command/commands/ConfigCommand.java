package com.georgev22.cosmicjars.command.commands;

import com.georgev22.cosmicjars.command.BaseCommand;
import org.jetbrains.annotations.NotNull;

public class ConfigCommand extends BaseCommand {

    public ConfigCommand() {
        super("config", "Makes changes to the config file.");
    }

    @Override
    public void execute(String @NotNull [] args) {
        if (args.length <= 1) {
            this.main.getLogger().info("Available config arguments: type <type> | implementation <implementation> | version <version> | jdkVersion <jdkVersion>");
            return;
        }
        if (args[0].equalsIgnoreCase("type")) {
            this.main.getConfig().set("server.type", args[1]);
            this.main.getLogger().info("Server type set to {}", args[1]);
        } else if (args[0].equalsIgnoreCase("implementation")) {
            this.main.getConfig().set("server.implementation", args[1]);
            this.main.getLogger().info("Server implementation set to {}", args[1]);
        } else if (args[0].equalsIgnoreCase("version")) {
            this.main.getConfig().set("server.version", args[1]);
            this.main.getLogger().info("Server version set to {}", args[1]);
        } else if (args[0].equalsIgnoreCase("jdkVersion")) {
            this.main.getConfig().set("server.jdkVersion", args[1]);
            this.main.getLogger().info("JDK version set to {}", args[1]);
        }

        this.main.saveConfig();
        this.main.reloadConfig();
        this.main.getLogger().info("Config reloaded");
    }
}
