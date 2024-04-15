package com.georgev22.cosmicjars;

import com.georgev22.cosmicjars.providers.*;
import com.georgev22.cosmicjars.providers.implementations.CentroJarProvider;
import com.georgev22.cosmicjars.providers.implementations.MohistProvider;
import com.georgev22.cosmicjars.providers.implementations.PaperProvider;
import com.georgev22.cosmicjars.providers.implementations.PurpurProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * Main class for CosmicJars application.
 */
public class Main {

    private final String PROPERTIES_FILE = "cosmicjars.properties";
    private final String COSMIC_JARS_FOLDER = "./cosmicJars/";
    private final Logger logger = LogManager.getLogger("CosmicJars");

    private static Main instance;

    /**
     * Gets the singleton instance of the CentersJars application.
     *
     * @return The singleton instance of the CentersJars application.
     */
    public static Main getInstance() {
        return instance;
    }

    /**
     * Main method to start the CosmicJars application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        instance = new Main();
        instance.start(args);
    }

    /**
     * Starts the CosmicJars application.
     */
    public void start(String[] args) {
        configureLogging();
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

        Properties properties = loadProperties();
        if (properties.isEmpty()) {
            properties = promptUserForServerDetails();
            saveProperties(properties);
        }

        List<String> cosmicArgs = Arrays.stream(args).filter(arg -> arg.startsWith("--cosmic")).toList();

        Optional<String> cosmicServerTypeArg = cosmicArgs.stream()
                .filter(arg -> arg.startsWith("--cosmicServerType="))
                .findFirst();

        Optional<String> cosmicServerImplementationArg = cosmicArgs.stream()
                .filter(arg -> arg.startsWith("--cosmicServerImplementation="))
                .findFirst();

        Optional<String> cosmicServerVersionArg = cosmicArgs.stream()
                .filter(arg -> arg.startsWith("--cosmicServerVersion="))
                .findFirst();


        String serverType = properties.getProperty("server.type");
        String serverImplementation = properties.getProperty("server.implementation");
        String version = properties.getProperty("server.version");

        if (cosmicServerTypeArg.isPresent()) {
            serverType = cosmicServerTypeArg.get().split("=")[1];
            logger.info("Overriding server type with: {}", serverType);
        }

        if (cosmicServerImplementationArg.isPresent()) {
            serverImplementation = cosmicServerImplementationArg.get().split("=")[1];
            logger.info("Overriding server implementation with: {}", serverImplementation);
        }

        if (cosmicServerVersionArg.isPresent()) {
            version = cosmicServerVersionArg.get().split("=")[1];
            logger.info("Overriding server version with: {}", version);
        }

        if (serverType == null || serverImplementation == null || version == null) {
            logger.error("Failed to get server details.");
            return;
        }

        logger.info("Server type: {}", serverType);
        logger.info("Server implementation: {}", serverImplementation);
        logger.info("Server version: {}", version);

        String fileName = downloadJar(serverType, serverImplementation, version);
        if (fileName != null) {
            startMinecraftServer(fileName, args);
        } else {
            logger.error("Failed to download jar.");
        }
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

    /**
     * Returns the path to the properties file.
     *
     * @return Path to the properties file.
     */
    public String getPropertiesFile() {
        return PROPERTIES_FILE;
    }

    /**
     * Configures logging for the application.
     */
    private void configureLogging() {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
    }

    /**
     * Loads properties from the properties file.
     *
     * @return Loaded properties.
     */
    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            File file = new File(PROPERTIES_FILE);
            if (!file.exists()) {
                return properties;
            }
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            logger.error("Failed to load properties file: {}", e.getMessage());
        }
        return properties;
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

            System.out.println("Properties file not found. Please provide the following details:");
            String serverType = lineReader.readLine("Server Type (e.g., servers): ");
            String serverImplementation = lineReader.readLine("Server Implementation (e.g., spigot): ");
            String version = lineReader.readLine("Version (e.g., latest): ");
            properties.setProperty("server.type", serverType);
            properties.setProperty("server.implementation", serverImplementation);
            properties.setProperty("server.version", version);
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
    private void saveProperties(@NotNull Properties properties) {
        try {
            properties.store(new FileOutputStream(PROPERTIES_FILE), null);
        } catch (IOException e) {
            logger.error("Failed to save properties file: {}", e.getMessage());
        }
    }

    /**
     * Downloads the JAR file.
     *
     * @param type           Server type.
     * @param implementation Server implementation.
     * @param version        Server version.
     * @return Absolute path of the downloaded JAR file, or null if download failed.
     */
    private @Nullable String downloadJar(String type, @NotNull String implementation, String version) {
        Provider provider;
        if (implementation.equalsIgnoreCase("purpur")) {
            provider = new PurpurProvider(type, implementation, version);
        } else if (implementation.equalsIgnoreCase("paper") || implementation.equalsIgnoreCase("folia")) {
            provider = new PaperProvider(type, implementation, version);
        } else if (implementation.equalsIgnoreCase("mohist") || implementation.equalsIgnoreCase("banner")) {
            provider = new MohistProvider(type, implementation, version);
        } else {
            provider = new CentroJarProvider(type, implementation, version);
        }
        return provider.downloadJar();
    }

    /**
     * Starts the Minecraft server using the specified JAR file and command line arguments.
     *
     * @param jarFile Path to the Minecraft server JAR file.
     * @param args    Command line arguments.
     */
    private void startMinecraftServer(String jarFile, String[] args) {
        try {
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();

            List<String> command = new ArrayList<>();
            command.add("java");
            command.addAll(vmArguments);
            command.add("-jar");
            command.add(jarFile);
            command.addAll(Arrays.stream(args).filter(arg -> !arg.startsWith("--cosmic")).toList());

            logger.info("Starting Minecraft server with command: {}", String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            Process pr = pb.start();

            int exitCode = pr.waitFor();
            logger.info("Minecraft server exited with code: {}", exitCode);
        } catch (IOException | InterruptedException e) {
            logger.error("Error starting Minecraft server: {}", e.getMessage());
        }
    }
}