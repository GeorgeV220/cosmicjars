package com.georgev22.cosmicjars;

import com.georgev22.cosmicjars.providers.*;
import com.georgev22.cosmicjars.providers.implementations.*;
import com.georgev22.cosmicjars.utilities.Utils;
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

    private static final File WORKING_DIRECTORY = new File(".");
    private final String PROPERTIES_FILE = "cosmicjars.properties";
    private Properties PROPERTIES;
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

        PROPERTIES = loadProperties();
        if (PROPERTIES.isEmpty()) {
            PROPERTIES = promptUserForServerDetails();
            saveProperties(PROPERTIES);
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


        String serverType = PROPERTIES.getProperty("server.type");
        String serverImplementation = PROPERTIES.getProperty("server.implementation");
        String version = PROPERTIES.getProperty("server.version");

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
     * Returns the properties object.
     *
     * @return Properties object.
     */
    public Properties getProperties() {
        return PROPERTIES;
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
    public void saveProperties(@NotNull Properties properties) {
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
    private @Nullable String downloadJar(@NotNull String type, @NotNull String implementation, @NotNull String version) {
        Provider provider;
        switch (type) {
            case "servers" -> provider = switch (implementation) {
                case "purpur" -> new PurpurProvider(type, implementation, version);
                case "paper", "folia" -> new PaperProvider(type, implementation, version);
                default -> new CentroJarProvider(type, implementation, version);
            };
            case "modded" -> provider = switch (implementation) {
                case "mohist", "banner" -> new MohistProvider(type, implementation, version);
                default -> new CentroJarProvider(type, implementation, version);
            };
            case "proxies" -> provider = switch (implementation) {
                case "velocity" -> new PaperProvider(type, implementation, version);
                case "bungeecord" -> new BungeeCordProvider(type, implementation, version);
                default -> new CentroJarProvider(type, implementation, version);
            };
            default -> provider = new CentroJarProvider(type, implementation, version);
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

            LinkedList<String> command = new LinkedList<>();

            if (PROPERTIES.getProperty("server.implementation").equalsIgnoreCase("forge")
                    || PROPERTIES.getProperty("server.implementation").equalsIgnoreCase("mohist")) {
                try (FileOutputStream fos = new FileOutputStream(new File(WORKING_DIRECTORY, "user_jvm_args.txt"))) {
                    StringBuilder sb = new StringBuilder();
                    for (String arg : vmArguments) {
                        sb.append(arg).append(" ");
                    }
                    fos.write(sb.toString().getBytes());
                } catch (IOException e) {
                    logger.error("Failed to write user JVM arguments: {}", e.getMessage());
                }
                File forgeRunner = new File(WORKING_DIRECTORY, Utils.isWindows() ? "run.bat" : "run.sh");
                if (!forgeRunner.exists()) {
                    logger.error("Could not find forge runner at: {}", forgeRunner.getAbsolutePath());
                    System.exit(1);
                }

                if (Utils.isWindows()) {
                    command.add("cmd.exe");
                    command.add("/c");
                }
                command.add(forgeRunner.getAbsolutePath());
            } else {
                command.add(getJavaExecutable());
                command.addAll(vmArguments);
                command.add("-jar");
                command.add(jarFile);
            }

            command.addAll(Arrays.stream(args).filter(arg -> !arg.startsWith("--cosmic")).toList());

            logger.info("Starting Minecraft server with command: {}", String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);

            Process process = pb
                    .inheritIO()
                    .start();

            Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

            while (process.isAlive()) {
                try {
                    int exitCode = process.waitFor();

                    if (exitCode != 0) {
                        logger.info("Minecraft server exited with code: {}", exitCode);
                    }

                    break;
                } catch (InterruptedException ignored) {
                }
            }
        } catch (IOException e) {
            logger.error("Error starting Minecraft server: {}", e.getMessage());
        }
    }

    /**
     * Gets the Java executable path.
     *
     * @return The Java executable path.
     */
    @NotNull
    private static String getJavaExecutable() {
        File binDir = new File(System.getProperty("java.home"), "bin");
        File javaExe = new File(binDir, "java");

        if (!javaExe.exists()) {
            javaExe = new File(binDir, "java.exe");
        }

        if (!javaExe.exists()) {
            getInstance().getLogger().error("We could not find your java executable inside '{}' - Using command 'java' instead", binDir.getAbsolutePath());

            return "java";
        }

        return javaExe.getAbsolutePath();
    }
}