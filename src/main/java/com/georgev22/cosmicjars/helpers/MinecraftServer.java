/**
 * Helper class for managing a Minecraft server process.
 */
package com.georgev22.cosmicjars.helpers;

import com.georgev22.cosmicjars.ConsoleFrame;
import com.georgev22.cosmicjars.CosmicJars;
import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.utilities.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class for managing a Minecraft server process.
 */
public class MinecraftServer {

    private final CosmicJars main = CosmicJars.getInstance();
    private final Provider provider;
    private final File workDir;
    private @Nullable File jarFile;
    private final String javaExecutable;
    private final String[] minecraftServerArguments;

    private Process minecraftServerProcess;
    private PrintWriter serverCommandWriter;

    /**
     * Constructs a MinecraftServer instance.
     *
     * @param provider                 The provider for the Minecraft server JAR file.
     * @param workDir                  The working directory for the server.
     * @param javaExecutable           The path to the Java executable.
     * @param minecraftServerArguments The arguments to be passed to the Minecraft server.
     */
    public MinecraftServer(
            @NotNull Provider provider,
            File workDir,
            String javaExecutable,
            String[] minecraftServerArguments
    ) {
        this.provider = provider;
        this.workDir = workDir;
        File jarFile = new File(provider.downloadJar());
        if (jarFile.exists()) {
            this.jarFile = jarFile;
        }
        this.javaExecutable = javaExecutable;
        this.minecraftServerArguments = minecraftServerArguments;
    }

    /**
     * Gets the working directory for the Minecraft server.
     *
     * @return The working directory.
     */
    public File getWorkDir() {
        return workDir;
    }

    /**
     * Gets the JAR file for the Minecraft server.
     *
     * @return The JAR file.
     */
    public @Nullable File getJarFile() {
        return jarFile;
    }

    /**
     * Gets the arguments for the Minecraft server.
     *
     * @return The arguments array.
     */
    public String[] getMinecraftServerArguments() {
        return minecraftServerArguments;
    }

    /**
     * Gets the provider for the Minecraft server.
     *
     * @return The provider.
     */
    public Provider getProvider() {
        return provider;
    }

    /**
     * Gets the process representing the Minecraft server.
     *
     * @return The Minecraft server process.
     */
    public Process getMinecraftServerProcess() {
        return minecraftServerProcess;
    }

    /**
     * Starts the Minecraft server.
     */
    public void start() {
        if (jarFile == null) {
            this.main.getLogger().error("Jar file is null!");
            return;
        }
        try {
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();

            LinkedList<String> command = new LinkedList<>();

            if (this.main.getConfig().getString("server.implementation", "").equalsIgnoreCase("forge")
                    || this.main.getConfig().getString("server.implementation", "").equalsIgnoreCase("mohist")) {
                try (FileOutputStream fos = new FileOutputStream(new File(this.workDir, "user_jvm_args.txt"))) {
                    StringBuilder sb = new StringBuilder();
                    for (String arg : vmArguments) {
                        sb.append(arg).append(" ");
                    }
                    fos.write(sb.toString().getBytes());
                } catch (IOException e) {
                    this.main.getLogger().error("Failed to write user JVM arguments: {}", e.getMessage());
                }
                File forgeRunner = new File(this.workDir, Utils.isWindows() ? "run.bat" : "run.sh");
                if (!forgeRunner.exists()) {
                    this.main.getLogger().error("Could not find forge runner at: {}", forgeRunner.getAbsolutePath());
                    Runtime.getRuntime().exit(1);
                }

                if (Utils.isWindows()) {
                    command.add("cmd.exe");
                    command.add("/c");
                }
                command.add(forgeRunner.getAbsolutePath());
            } else {
                command.add(this.javaExecutable);
                command.addAll(vmArguments);
                command.add("-jar");
                command.add(jarFile.getAbsolutePath());
            }

            command.addAll(Arrays.stream(this.minecraftServerArguments).filter(arg -> !arg.startsWith("--cosmic")).toList());

            this.main.getLogger().info("Starting Minecraft server with command: {}", String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);

            if (CosmicJars.getInstance().isGUI()) {
                minecraftServerProcess = pb.start();
                ConsoleFrame.getInstance().addCpuAndMemoryUsage(minecraftServerProcess.pid());
            } else {
                minecraftServerProcess = pb.inheritIO().start();
            }

            serverCommandWriter = new PrintWriter(minecraftServerProcess.getOutputStream());
            InputStream processOutput = minecraftServerProcess.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(processOutput));

            if (CosmicJars.getInstance().isGUI()) {
                new Thread(() -> {
                    try {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String finalLine = line;
                            SwingUtilities.invokeLater(() -> ConsoleFrame.getInstance().printToConsole(finalLine));
                        }
                    } catch (IOException e) {
                        this.main.getLogger().error("Error reading Minecraft server output: {}", e.getMessage());
                    }
                }).start();
            } else {
                while (minecraftServerProcess.isAlive()) {
                    try {
                        int exitCode = minecraftServerProcess.waitFor();

                        if (exitCode != 0) {
                            this.main.getLogger().info("Minecraft server exited with code: {}", exitCode);
                        }

                        break;
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (minecraftServerProcess != null && minecraftServerProcess.isAlive()) {
                    minecraftServerProcess.destroy();
                }
            }));
        } catch (IOException e) {
            this.main.getLogger().error("Error starting Minecraft server: {}", e.getMessage());
        }
    }

    /**
     * Sends a command to the Minecraft server.
     *
     * @param command The command to send.
     */
    public void sendCommandToServer(String[] command) {
        if (minecraftServerProcess != null && minecraftServerProcess.isAlive()) {
            serverCommandWriter.println(String.join(" ", command));
            serverCommandWriter.flush();
        } else {
            this.main.getLogger().error("Minecraft server is not running");
        }
    }
}
