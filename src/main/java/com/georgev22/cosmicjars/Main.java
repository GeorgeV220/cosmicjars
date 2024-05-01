package com.georgev22.cosmicjars;

import com.georgev22.cosmicjars.helpers.MinecraftServer;
import com.georgev22.cosmicjars.providers.*;
import com.georgev22.cosmicjars.utilities.JDKUtilities;
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
public class Main {
    private static Main instance;

    private final File WORKING_DIRECTORY = new File(".");
    private final String PROPERTIES_FILE = "cosmicjars.properties";
    private final String COSMIC_JARS_FOLDER = "./cosmicJars/";
    private final Logger logger;
    private final boolean gui;
    private final String[] programArguments;
    private final JDKUtilities jdkUtilities;

    private Properties PROPERTIES;
    private MinecraftServer minecraftServer;

    private String serverType, serverImplementation, serverVersion;

    /**
     * Gets the singleton instance of the CentersJars application.
     *
     * @return The singleton instance of the CentersJars application.
     */
    public static Main getInstance() {
        return instance;
    }

    public static void setInstance(Main instance) {
        Main.instance = instance;
    }

    /**
     * Main method to start the CosmicJars application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new Main(args);
    }

    public Main(String[] args) {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        instance = this;
        this.jdkUtilities = new JDKUtilities();
        this.programArguments = args;
        List<String> cosmicArgs = Arrays.stream(this.programArguments).filter(arg -> arg.startsWith("--cosmic")).toList();
        Optional<String> guiArg = Arrays.stream(this.programArguments).filter(arg -> arg.startsWith("--cosmicgui")).findFirst();
        this.gui = guiArg.isPresent() && guiArg.get().equals("--cosmicgui");
        if (gui) {
            SwingUtilities.invokeLater(() -> {
                ConsoleFrame frame = new ConsoleFrame();
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

        PROPERTIES = loadProperties();
        if (PROPERTIES.isEmpty()) {
            PROPERTIES = promptUserForServerDetails();
            saveProperties(PROPERTIES);
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


        String serverType = PROPERTIES.getProperty("server.type");
        String serverImplementation = PROPERTIES.getProperty("server.implementation");
        String serverVersion = PROPERTIES.getProperty("server.version");

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
     * Returns the path to the properties file.
     *
     * @return Path to the properties file.
     */
    public String getPropertiesFile() {
        return PROPERTIES_FILE;
    }

    /**
     * Returns the properties object.
     *
     * @return Properties object.
     */
    public Properties getProperties() {
        return PROPERTIES;
    }

    /**
     * Loads properties from the properties file.
     *
     * @return Loaded properties.
     */
    public Properties loadProperties() {
        if (PROPERTIES == null) {
            try {
                PROPERTIES = new Properties();
                File file = new File(PROPERTIES_FILE);
                if (!file.exists()) {
                    return PROPERTIES;
                }
                PROPERTIES.load(new FileInputStream(file));
            } catch (IOException e) {
                logger.error("Failed to load properties file: {}", e.getMessage());
            }
        }
        return PROPERTIES;
    }

    /**
     * Prompts the user for server details and returns them as properties.
     *
     * @return Properties containing server details.
     */
    private @NotNull Properties promptUserForServerDetails() {
        Properties properties = new Properties();
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();

            logger.info("Properties file not found. Please provide the following details:");
            logger.info("Server Type (e.g., servers): ");
            String serverType = lineReader.readLine();
            logger.info("Selected Server Type: {}", serverType);

            logger.info("Server Implementation (e.g., spigot): ");
            String serverImplementation = lineReader.readLine();
            logger.info("Selected Server Implementation: {}", serverImplementation);

            logger.info("Server Version (e.g., latest): ");
            String version = lineReader.readLine();
            logger.info("Selected Server Version: {}", version);

            logger.info("JDK Version (e.g., 17): ");
            String jdkVersion = lineReader.readLine();
            logger.info("Selected JDK Version: {}", jdkVersion);

            properties.setProperty("server.type", serverType);
            properties.setProperty("server.implementation", serverImplementation);
            properties.setProperty("server.version", version);
            properties.setProperty("jdk.version", jdkVersion);
        } catch (IOException e) {
            logger.error("Error prompting user for server details: {}", e.getMessage());
        }
        return properties;
    }

    /**
     * Saves properties to the properties file.
     *
     * @param properties Properties to be saved.
     */
    public void saveProperties(@NotNull Properties properties) {
        try {
            properties.store(new FileOutputStream(PROPERTIES_FILE), null);
        } catch (IOException e) {
            logger.error("Failed to save properties file: {}", e.getMessage());
        }
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
                String[] configArguments = Arrays.copyOfRange(command, 1, command.length);
                if (configArguments.length < 2) {
                    this.logger.info("Available config properties:");
                    for (String key : this.getProperties().stringPropertyNames()) {
                        this.logger.info("{}", key);
                    }
                    this.logger.info("Config: {}", this.getProperties().toString());
                    this.logger.info("Working directory: {}", this.WORKING_DIRECTORY.getAbsolutePath());
                    this.logger.info("Available config arguments: <key> <value>");
                    return;
                }
                this.getProperties().setProperty(configArguments[0], configArguments[1]);
                this.logger.info("Config property '{}' set to '{}'", configArguments[0], configArguments[1]);
                this.saveProperties(this.getProperties());
            }
            default -> this.logger.info("Unknown command: {}, type 'help' for help", String.join(" ", command));
        }
    }
}