package com.georgev22.centrojars;

import com.georgev22.centrojars.providers.*;
import com.georgev22.centrojars.providers.implementations.CentroJarProvider;
import com.georgev22.centrojars.providers.implementations.MohistProvider;
import com.georgev22.centrojars.providers.implementations.PaperProvider;
import com.georgev22.centrojars.providers.implementations.PurpurProvider;
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
 * Main class for CentroJars application.
 */
public class Main {

    private final String PROPERTIES_FILE = "centrojars.properties";
    private final String CENTRO_JARS_FOLDER = "./centroJars/";
    private final Logger logger = LogManager.getLogger("CentroJars");

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
     * Main method to start the CentroJars application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        instance = new Main();
        instance.start(args);
    }

    /**
     * Starts the CentroJars application.
     */
    public void start(String[] args) {
        configureLogging();
        logger.info("CentroJars starting...");
        logger.info("""
                                                                                                                                          \s
                                                                                              ,---._                                      \s
                  ,----..                               ___                                 .-- -.' \\                                     \s
                 /   /   \\                            ,--.'|_                               |    |   :                                    \s
                |   :     :                  ,---,    |  | :,'    __  ,-.    ,---.          :    ;   |                __  ,-.             \s
                .   |  ;. /              ,-+-. /  |   :  : ' :  ,' ,'/ /|   '   ,'\\         :        |              ,' ,'/ /|   .--.--.   \s
                .   ; /--`     ,---.    ,--.'|'   | .;__,'  /   '  | |' |  /   /   |        |    :   :   ,--.--.    '  | |' |  /  /    '  \s
                ;   | ;       /     \\  |   |  ,"' | |  |   |    |  |   ,' .   ; ,. :        :           /       \\   |  |   ,' |  :  /`./  \s
                |   : |      /    /  | |   | /  | | :__,'| :    '  :  /   '   | |: :        |    ;   | .--.  .-. |  '  :  /   |  :  ;_    \s
                .   | '___  .    ' / | |   | |  | |   '  : |__  |  | '    '   | .; :    ___ l           \\__\\/: . .  |  | '     \\  \\    `. \s
                '   ; : .'| '   ;   /| |   | |  |/    |  | '.'| ;  : |    |   :    |  /    /\\    J   :  ," .--.; |  ;  : |      `----.   \\\s
                '   | '/  : '   |  / | |   | |--'     ;  :    ; |  , ;     \\   \\  /  /  ../  `..-    , /  /  ,.  |  |  , ;     /  /`--'  /\s
                |   :    /  |   :    | |   |/         |  ,   /   ---'       `----'   \\    \\         ; ;  :   .'   \\  ---'     '--'.     / \s
                 \\   \\ .'    \\   \\  /  '---'           ---`-'                         \\    \\      ,'  |  ,     .-./             `--'---'  \s
                  `---`       `----'                                                   "---....--'     `--`---'                           \s
                                                                                                                                          \s
                """);
        logger.info("Made with love by George V. https://github.com/GeorgeV220");
        logger.info("https://centrojars.com/");

        Properties properties = loadProperties();
        if (properties.isEmpty()) {
            properties = promptUserForServerDetails();
            saveProperties(properties);
        }

        List<String> centroArgs = Arrays.stream(args).filter(arg -> arg.startsWith("--centro")).toList();

        Optional<String> centroServerTypeArg = centroArgs.stream()
                .filter(arg -> arg.startsWith("--centroServerType="))
                .findFirst();

        Optional<String> centroServerImplementationArg = centroArgs.stream()
                .filter(arg -> arg.startsWith("--centroServerImplementation="))
                .findFirst();

        Optional<String> centroServerVersionArg = centroArgs.stream()
                .filter(arg -> arg.startsWith("--centroServerVersion="))
                .findFirst();


        String serverType = properties.getProperty("server.type");
        String serverImplementation = properties.getProperty("server.implementation");
        String version = properties.getProperty("server.version");

        if (centroServerTypeArg.isPresent()) {
            serverType = centroServerTypeArg.get().split("=")[1];
            logger.info("Overriding server type with: {}", serverType);
        }

        if (centroServerImplementationArg.isPresent()) {
            serverImplementation = centroServerImplementationArg.get().split("=")[1];
            logger.info("Overriding server implementation with: {}", serverImplementation);
        }

        if (centroServerVersionArg.isPresent()) {
            version = centroServerVersionArg.get().split("=")[1];
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
     * Returns the path to the CentroJars folder.
     *
     * @return Path to the CentroJars folder.
     */
    public String getCentroJarsFolder() {
        return CENTRO_JARS_FOLDER;
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
            System.out.println("Check https://centrojars.com/ for more details.");
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
     * Downloads the JAR file from the CentroJars API.
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
            command.addAll(Arrays.stream(args).filter(arg -> !arg.startsWith("--centro")).toList());

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