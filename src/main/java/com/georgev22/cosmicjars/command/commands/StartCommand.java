package com.georgev22.cosmicjars.command.commands;

import com.georgev22.cosmicjars.command.BaseCommand;
import com.georgev22.cosmicjars.helpers.MinecraftServer;
import com.georgev22.cosmicjars.providers.Provider;

public class StartCommand extends BaseCommand {

    public StartCommand() {
        super("start", "Starts the Minecraft server");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("help")) {
                this.main.getLogger().info("Available start arguments: <type> <implementation> <version>");
                return;
            }
        }
        if (args.length < 3) {
            if (this.main.getMinecraftServer() != null) {
                this.main.getLogger().info("Starting Minecraft server with the current settings.");
                this.main.getMinecraftServer().start();
            } else {
                this.main.getLogger().info(
                        "Starting Minecraft server with the following settings: server.type={}, server.implementation={}, server.version={}",
                        this.main.getConfig().getString("server.type"),
                        this.main.getConfig().getString("server.implementation"),
                        this.main.getConfig().getString("server.version")
                );
                this.main.setMinecraftServer(new MinecraftServer(
                        Provider.getProvider(
                                this.main.getConfig().getString("server.type", "mcjars"),
                                this.main.getConfig().getString("server.implementation", "Vanilla"),
                                this.main.getConfig().getString("server.version", "1.21.4")
                        ),
                        this.main.getWorkDir(),
                        this.main.getJDKUtilities().getJavaExecutable(),
                        this.main.getProgramArguments()
                ));
                this.main.getMinecraftServer().start();
            }
        } else {
            this.main.getLogger().info("Starting Minecraft server with the following settings: type={}, implementation={}, version={}", args[0], args[1], args[2]);
            this.main.setMinecraftServer(new MinecraftServer(
                    Provider.getProvider(args[0], args[1], args[2]),
                    this.main.getWorkDir(),
                    this.main.getJDKUtilities().getJavaExecutable(),
                    this.main.getProgramArguments()
            ));
            if (this.main.getMinecraftServer() == null) {
                this.main.getLogger().info("Failed to start Minecraft server with the following settings: type={}, implementation={}, version={}", args[0], args[1], args[2]);
                return;
            }
            this.main.getMinecraftServer().start();
        }
    }
}
