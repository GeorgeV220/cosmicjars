package com.georgev22.cosmicjars.utilities;

import com.georgev22.cosmicjars.Main;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JDKUtilities {

    private final Main main = Main.getInstance();
    private final String ADOPT_OPENJDK_URL_TEMPLATE = "https://api.adoptopenjdk.net/v3/binary/latest/%s/ga/%s/%s/jdk/hotspot/normal/adoptopenjdk";

    /**
     * Retrieves the version of the Java runtime.
     *
     * @return The version of the Java runtime.
     */
    public String getJDKVersion() {
        return System.getProperty("java.version");
    }

    /**
     * Retrieves the path of the Java executable.
     *
     * @return The path of the Java executable.
     */
    public @NotNull String getJavaExecutable() {
        String jdkVersion = this.main.getConfig().getString("server.jdkVersion", "17");
        File jdkDir = new File(new File(this.main.getWorkDir(), ".jdks"), jdkVersion);
        if (!jdkDir.exists()) {
            this.main.getLogger().error("JDK version '{}' does not exist - Downloading it", jdkVersion);
            jdkDir = this.downloadJDK(jdkVersion);
        }

        File binDir;

        if (jdkDir == null) {
            this.main.getLogger().error("JDK version '{}' does not exist - Using command 'java.home' system property instead", jdkVersion);
            binDir = new File(System.getProperty("java.home"), "bin");
        } else {
            binDir = new File(jdkDir, "bin");
        }

        File javaExe = new File(binDir, "java");

        if (!javaExe.exists()) {
            javaExe = new File(binDir, "java.exe");
        }

        if (!javaExe.exists()) {
            this.main.getLogger().error("We could not find your java executable inside '{}' - Using command 'java' instead", binDir.getAbsolutePath());

            return "java";
        }

        this.main.getLogger().info("Java executable found: {}", javaExe.getAbsolutePath());

        return javaExe.getAbsolutePath();
    }

    /**
     * Downloads the specified version of the AdoptOpenJDK and extracts it to the .jdks directory
     *
     * @param version The version of the JDK to download
     * @return The path to the downloaded JDK
     */
    public File downloadJDK(String version) {
        String url = String.format(ADOPT_OPENJDK_URL_TEMPLATE, version, this.getPlatform(), this.getArchitecture());
        String fileName = String.format("jdk-%s.zip", version);
        File jdkDir = new File(new File(this.main.getWorkDir(), ".jdks"), version);
        String outputPath = jdkDir.getAbsolutePath() + "/";

        try {
            Path directory = Paths.get(jdkDir.getAbsolutePath());
            Files.createDirectories(directory);

            String downloadedFilePath = Utils.downloadFile(url, outputPath, fileName);

            extractFirstFolderFromZip(downloadedFilePath, outputPath);

            this.main.getLogger().info("JDK {} downloaded successfully to: {}", version, outputPath);
            return new File(outputPath);
        } catch (IOException e) {
            this.main.getLogger().log(Level.ERROR, "Failed to download JDK {}: {}", version, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Extracts the first folder from a zip file
     *
     * @param zipFilePath zip file path
     * @param outputPath  output path
     * @throws IOException if an I/O error occurs
     */
    private void extractFirstFolderFromZip(String zipFilePath, String outputPath) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            String firstFolderName = null;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    String entryName = entry.getName();
                    if (firstFolderName == null) {
                        firstFolderName = entryName;
                    } else if (!entryName.startsWith(firstFolderName)) {
                        break;
                    }
                }
            }

            if (firstFolderName != null) {
                Enumeration<? extends ZipEntry> entriesAgain = zipFile.entries();
                while (entriesAgain.hasMoreElements()) {
                    ZipEntry entry = entriesAgain.nextElement();
                    if (!entry.isDirectory() && entry.getName().startsWith(firstFolderName)) {
                        Path destPath = Paths.get(outputPath, entry.getName().substring(firstFolderName.length()));
                        Files.createDirectories(destPath.getParent());
                        Files.copy(zipFile.getInputStream(entry), destPath);
                    }
                }
            }
        }
    }

    /**
     * Returns the platform name based on the current operating system.
     *
     * @return The platform name
     */
    public String getPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "windows";
        } else if (os.contains("mac")) {
            return "mac";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return "linux";
        } else {
            throw new IllegalStateException("Unknown OS: " + os);
        }
    }

    /**
     * Returns the architecture name based on the current operating system.
     *
     * @return The architecture name
     */
    public String getArchitecture() {
        String arch = System.getProperty("os.arch").toLowerCase();
        return arch.contains("64") ? "x64" : "x32";
    }
}