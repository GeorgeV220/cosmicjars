package com.georgev22.cosmicjars;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme;
import com.georgev22.cosmicjars.helpers.MinecraftServer;
import com.georgev22.cosmicjars.providers.*;
import com.georgev22.cosmicjars.utilities.JDKUtilities;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.library.yaml.file.YamlConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Main class for CosmicJars application.
 */
public class CosmicJars {
    private static CosmicJars instance;

    private final File WORKING_DIRECTORY = new File(".");
    private FileConfiguration fileConfiguration;
    private final File configFile;
    private final String COSMIC_JARS_FOLDER = "./cosmicJars/";
    private final Logger logger;
    private final boolean gui;
    private final String[] programArguments;
    private final JDKUtilities jdkUtilities;
    private MinecraftServer minecraftServer;

    private String serverType, serverImplementation, serverVersion;

    /**
     * Gets the singleton instance of the CentersJars application.
     *
     * @return The singleton instance of the CentersJars application.
     */
    public static CosmicJars getInstance() {
        return instance;
    }

    public static void setInstance(CosmicJars instance) {
        CosmicJars.instance = instance;
    }

    /**
     * Main method to start the CosmicJars application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new CosmicJars(args);
    }

    public CosmicJars(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatGitHubDarkIJTheme());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        instance = this;
        this.jdkUtilities = new JDKUtilities();
        this.programArguments = args;
        List<String> cosmicArgs = Arrays.stream(this.programArguments).filter(arg -> arg.startsWith("--cosmic")).toList();
        Optional<String> guiArg = Arrays.stream(this.programArguments).filter(arg -> arg.startsWith("--cosmicgui")).findFirst();
        this.gui = guiArg.isPresent() && guiArg.get().equals("--cosmicgui");
        if (gui) {
            SwingUtilities.invokeLater(() -> {
                CosmicJarsFrame frame = new CosmicJarsFrame();
                frame.setVisible(true);
            });
        }
        this.logger = LogManager.getLogger("CosmicJars");
        logger.info("CosmicJars starting...");
        logger.info("""

                                                                                                                                               \s
                                                                                                   ,---._                                      \s
                  ,----..                                     ____                               .-- -.' \\                                     \s
                 /   /   \\                                  ,'  , `.   ,--,                      |    |   :                                    \s
                |   :     :    ,---.                     ,-+-,.' _ | ,--.'|                      :    ;   |                __  ,-.             \s
                .   |  ;. /   '   ,'\\    .--.--.      ,-+-. ;   , || |  |,                       :        |              ,' ,'/ /|   .--.--.   \s
                .   ; /--`   /   /   |  /  /    '    ,--.'|'   |  || `--'_        ,---.          |    :   :   ,--.--.    '  | |' |  /  /    '  \s
                ;   | ;     .   ; ,. : |  :  /`./   |   |  ,', |  |, ,' ,'|      /     \\         :           /       \\   |  |   ,' |  :  /`./  \s
                |   : |     '   | |: : |  :  ;_     |   | /  | |--'  '  | |     /    / '         |    ;   | .--.  .-. |  '  :  /   |  :  ;_    \s
                .   | '___  '   | .; :  \\  \\    `.  |   : |  | ,     |  | :    .    ' /      ___ l           \\__\\/: . .  |  | '     \\  \\    `. \s
                '   ; : .'| |   :    |   `----.   \\ |   : |  |/      '  : |__  '   ; :__   /    /\\    J   :  ," .--.; |  ;  : |      `----.   \\\s
                '   | '/  :  \\   \\  /   /  /`--'  / |   | |`-'       |  | '.'| '   | '.'| /  ../  `..-    , /  /  ,.  |  |  , ;     /  /`--'  /\s
                |   :    /    `----'   '--'.     /  |   ;/           ;  :    ; |   :    : \\    \\         ; ;  :   .'   \\  ---'     '--'.     / \s
                 \\   \\ .'                `--'---'   '---'            |  ,   /   \\   \\  /   \\    \\      ,'  |  ,     .-./             `--'---'  \s
                  `---`                                               ---`-'     `----'     "---....--'     `--`---'                           \s
                                                                                                                                               \s
                """);
        logger.info("Made with love by George V. https://github.com/GeorgeV220");

        configFile = new File(WORKING_DIRECTORY, "centroJars.yml");
        if (!configFile.exists()) {
            this.fileConfiguration = promptUserForServerDetails();
        } else {
            this.fileConfiguration = YamlConfiguration.loadConfiguration(this.configFile);
        }

        Optional<String> cosmicServerTypeArg = cosmicArgs.stream()
                .filter(arg -> arg.startsWith("--cosmicServerType="))
                .findFirst();

        Optional<String> cosmicServerImplementationArg = cosmicArgs.stream()
                .filter(arg -> arg.startsWith("--cosmicServerImplementation="))
                .findFirst();

        Optional<String> cosmicServerVersionArg = cosmicArgs.stream()
                .filter(arg -> arg.startsWith("--cosmicServerVersion="))
                .findFirst();

        String serverType = fileConfiguration.getString("server.type");
        String serverImplementation = fileConfiguration.getString("server.implementation");
        String serverVersion = fileConfiguration.getString("server.version");

        if (cosmicServerTypeArg.isPresent()) {
            serverType = cosmicServerTypeArg.get().split("=")[1];
            logger.info("Overriding server type with: {}", serverType);
        }

        if (cosmicServerImplementationArg.isPresent()) {
            serverImplementation = cosmicServerImplementationArg.get().split("=")[1];
            logger.info("Overriding server implementation with: {}", serverImplementation);
        }

        if (cosmicServerVersionArg.isPresent()) {
            serverVersion = cosmicServerVersionArg.get().split("=")[1];
            logger.info("Overriding server version with: {}", serverVersion);
        }

        if (serverType == null || serverImplementation == null || serverVersion == null) {
            logger.error("Failed to get server details.");
            return;
        }

        this.serverType = serverType;
        this.serverImplementation = serverImplementation;
        this.serverVersion = serverVersion;

        String[] minecraftServerArguments = Arrays.stream(this.programArguments).filter(arg -> !arg.startsWith("--cosmic")).toArray(String[]::new);
        this.minecraftServer = new MinecraftServer(
                Provider.getProvider(serverType, serverImplementation, serverVersion),
                WORKING_DIRECTORY,
                jdkUtilities.getJavaExecutable(),
                minecraftServerArguments
        );
        this.minecraftServer.start();
    }

    /**
     * Returns the logger for the application.
     *
     * @return Logger for the application.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns the path to the CosmicJars folder.
     *
     * @return Path to the CosmicJars folder.
     */
    public String getCosmicJarsFolder() {
        return COSMIC_JARS_FOLDER;
    }

    public File getWorkDir() {
        return this.WORKING_DIRECTORY;
    }

    /**
     * Prompts the user for server details.
     *
     * @return YamlConfiguration with the server details.
     */
    private @NotNull YamlConfiguration promptUserForServerDetails() {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            if (this.configFile.createNewFile()) {
                yamlConfiguration = new YamlConfiguration();
                if (!gui) {
                    Terminal terminal = TerminalBuilder.builder().system(true).build();
                    LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();

                    logger.info("Config file not found. Please provide the following details:");
                    logger.info("Server Type (e.g., servers): ");
                    String serverType = lineReader.readLine();
                    logger.info("Selected Server Type: {}", serverType);

                    logger.info("Server Implementation (e.g., spigot): ");
                    String serverImplementation = lineReader.readLine();
                    logger.info("Selected Server Implementation: {}", serverImplementation);

                    logger.info("Server Version (e.g., 1.20.4): ");
                    String version = lineReader.readLine();
                    logger.info("Selected Server Version: {}", version);

                    logger.info("JDK Version (e.g., 17): ");
                    String jdkVersion = lineReader.readLine();
                    logger.info("Selected JDK Version: {}", jdkVersion);

                    yamlConfiguration.set("server.type", serverType);
                    yamlConfiguration.set("server.implementation", serverImplementation);
                    yamlConfiguration.set("server.version", version);
                    yamlConfiguration.set("server.jdkVersion", jdkVersion);
                } else {
                    logger.info("Config file not found. Please set the details using the command config");
                }
            }


        } catch (IOException e) {
            logger.error("Error prompting user for server details: {}", e.getMessage());
        }
        return yamlConfiguration;
    }

    /**
     * Returns the program arguments.
     *
     * @return The program arguments.
     */
    public String[] getProgramArguments() {
        return programArguments;
    }

    /**
     * Returns true if the program is running in GUI mode.
     *
     * @return True if the program is running in GUI mode.
     */
    public boolean isGUI() {
        return gui;
    }

    /**
     * Returns the MinecraftServer instance.
     *
     * @return The MinecraftServer instance.
     */
    public @Nullable MinecraftServer getMinecraftServer() {
        return this.minecraftServer;
    }

    /**
     * Returns the JDKUtilities instance.
     *
     * @return The JDKUtilities instance.
     */
    public JDKUtilities getJDKUtilities() {
        return jdkUtilities;
    }

    public FileConfiguration getConfig() {
        return this.fileConfiguration;
    }

    public void saveConfig() {
        try {
            this.fileConfiguration.save(this.configFile);
        } catch (IOException e) {
            this.logger.error("Error saving centroJars.yml: {}", e.getMessage());
        }
    }

    public void reloadConfig() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public void sendCommand(String[] command) {
        this.logger.info("Received command: {}", String.join(" ", command));
        switch (command[0]) {
            case "exit" -> {
                Runtime runtime = Runtime.getRuntime();
                runtime.exit(0);
            }
            case "help" -> this.logger.info("Available commands: \nexit \nstart \nhelp \nconfig");
            case "start" -> {
                String[] startArguments = Arrays.copyOfRange(command, 1, command.length);
                if (startArguments.length != 0) {
                    if (startArguments[0].equalsIgnoreCase("help")) {
                        this.logger.info("Available start arguments: <type> <implementation> <version>");
                        return;
                    }
                }
                if (startArguments.length < 3) {
                    if (this.minecraftServer != null) {
                        this.logger.info("Starting Minecraft server with the current settings.");
                        this.minecraftServer.start();
                    } else {
                        this.logger.info("Starting Minecraft server with the following settings: server.type={}, server.implementation={}, server.version={}", serverType, serverImplementation, serverVersion);
                        this.minecraftServer = new MinecraftServer(
                                Provider.getProvider(serverType, serverImplementation, serverVersion),
                                WORKING_DIRECTORY,
                                jdkUtilities.getJavaExecutable(),
                                programArguments
                        );
                        this.minecraftServer.start();
                    }
                } else {
                    this.logger.info("Starting Minecraft server with the following settings: type={}, implementation={}, version={}", startArguments[0], startArguments[1], startArguments[2]);
                    this.minecraftServer = new MinecraftServer(
                            Provider.getProvider(startArguments[0], startArguments[1], startArguments[2]),
                            WORKING_DIRECTORY,
                            jdkUtilities.getJavaExecutable(),
                            programArguments
                    );
                    this.minecraftServer.start();
                }
            }
            case "config" -> {
                String[] args = Arrays.copyOfRange(command, 1, command.length);
                if (args.length <= 1) {
                    this.logger.info("Available config arguments: type <type> | implementation <implementation> | version <version> | jdkVersion <jdkVersion>");
                    return;
                }
                if (args[0].equalsIgnoreCase("type")) {
                    this.getConfig().set("server.type", args[1]);
                    this.logger.info("Server type set to {}", args[1]);
                } else if (args[0].equalsIgnoreCase("implementation")) {
                    this.getConfig().set("server.implementation", args[1]);
                    this.logger.info("Server implementation set to {}", args[1]);
                } else if (args[0].equalsIgnoreCase("version")) {
                    this.getConfig().set("server.version", args[1]);
                    this.logger.info("Server version set to {}", args[1]);
                } else if (args[0].equalsIgnoreCase("jdkVersion")) {
                    this.getConfig().set("server.jdkVersion", args[1]);
                    this.logger.info("JDK version set to {}", args[1]);
                }

                this.saveConfig();

            }
            default -> this.logger.info("Unknown command: {}, type 'help' for help", String.join(" ", command));
        }
    }
}