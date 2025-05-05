package com.georgev22.cosmicjars.utilities;

import com.georgev22.cosmicjars.CosmicJars;
import com.georgev22.cosmicjars.helpers.Platform;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JDKUtilities {

    private final CosmicJars main = CosmicJars.getInstance();

    /**
     * Retrieves a list of available JDK versions from the Adoptium API.
     *
     * @return An array of available JDK versions.
     */
    public String[] getOnlineJDKVersions() {
        List<String> versions = new ArrayList<>();
        try {
            URL url = new URL("https://api.adoptium.net/v3/info/available_releases");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
                Set<Integer> uniqueVersions = new HashSet<>();

                JsonArray availableLTSVersions = jsonObject.getAsJsonArray("available_lts_releases");
                for (int i = 0; i < availableLTSVersions.size(); i++) {
                    int ltsVersion = availableLTSVersions.get(i).getAsInt();
                    versions.add(ltsVersion + " (LTS)");
                    uniqueVersions.add(ltsVersion);
                }

                JsonArray availableVersions = jsonObject.getAsJsonArray("available_releases");
                for (int i = 0; i < availableVersions.size(); i++) {
                    int version = availableVersions.get(i).getAsInt();
                    if (!uniqueVersions.contains(version)) {
                        versions.add(String.valueOf(version));
                    }
                }
            }
            conn.disconnect();
        } catch (IOException e) {
            this.main.getLogger().log(Level.ERROR, () -> String.format("Failed to get JDK versions: %s", e.getMessage()), e);
        }
        return versions.toArray(new String[0]);
    }

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
        Platform.OS os = Platform.getCurrentOS();
        String arch = Platform.getCurrentArchitecture();
        String platformId = String.format("%s-%s-%s", jdkVersion, os.getOS(), arch);

        File jdkDir = new File(new File(this.main.getWorkDir(), ".jdks"), platformId);
        if (!jdkDir.exists()) {
            this.main.getLogger().error("JDK version '{}' for platform '{}' and arch '{}' does not exist - Downloading it", jdkVersion, platformId, arch);
            jdkDir = this.downloadJDK(jdkVersion, os, arch);
        }

        File binDir;

        if (jdkDir == null) {
            this.main.getLogger().error("JDK version '{}' does not exist - Using command 'java.home' system property instead", jdkVersion);
            binDir = new File(System.getProperty("java.home"), "bin");
        } else {
            binDir = new File(jdkDir, "bin");
        }

        File java = new File(binDir, "java");

        if (!java.exists() && os.equals(Platform.OS.WINDOWS)) {
            java = new File(binDir, "java.exe");
        }

        if (!java.exists()) {
            this.main.getLogger().error("We could not find your java executable inside '{}' - Using command 'java' instead", binDir.getAbsolutePath());
            return "java";
        }

        if (os.equals(Platform.OS.LINUX)) {
            try {
                Set<PosixFilePermission> permissions = EnumSet.of(
                        PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE,
                        PosixFilePermission.OWNER_WRITE, PosixFilePermission.GROUP_WRITE, PosixFilePermission.OTHERS_WRITE,
                        PosixFilePermission.OWNER_READ, PosixFilePermission.GROUP_READ, PosixFilePermission.OTHERS_READ
                );
                Files.setPosixFilePermissions(java.toPath(), permissions);
            } catch (IOException e) {
                this.main.getLogger().error("Failed to set permissions on java executable: {}", e.getMessage());
            }
        }

        this.main.getLogger().info("Java executable found: {}", java.getAbsolutePath());
        return java.getAbsolutePath();
    }

    /**
     * Downloads the specified version of the AdoptOpenJDK and extracts it to the .jdks directory
     *
     * @param version The version of the JDK to download
     * @return The path to the downloaded JDK
     */
    public File downloadJDK(String version, Platform.@NotNull OS os, String arch) {
        String url = String.format(
                "https://api.adoptopenjdk.net/v3/binary/latest/%s/ga/%s/%s/jdk/hotspot/normal/adoptopenjdk",
                version,
                os.getOS(),
                arch
        );
        String fileName = String.format(
                os.equals(Platform.OS.LINUX)
                        ? "jdk-%s-linux-%s.tar.gz"
                        : "jdk-%s-%s.zip",
                version,
                arch
        );

        String platformId = String.format("%s-%s-%s", version, os.getOS(), arch);
        File jdkDir = new File(new File(this.main.getWorkDir(), ".jdks"), platformId);
        String outputPath = jdkDir.getAbsolutePath() + "/";

        try {
            Files.createDirectories(Paths.get(jdkDir.getAbsolutePath()));
            String downloadedFilePath = Utils.downloadFile(url, outputPath, fileName);
            if (os.equals(Platform.OS.WINDOWS)) {
                extractFirstFolderFromZip(downloadedFilePath, outputPath);
            } else {
                extractFirstFolderFromTarGz(downloadedFilePath, outputPath);
            }


            this.main.getLogger().info("JDK {} downloaded successfully to: {}", version, outputPath);
            return new File(outputPath);
        } catch (IOException | ArchiveException e) {
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
     * Extracts the contents of the first folder from a tar.gz file
     *
     * @param tarGzFilePath tar.gz file path
     * @param outputPath    output path
     * @throws IOException if an I/O error occurs
     */
    private void extractFirstFolderFromTarGz(String tarGzFilePath, String outputPath) throws IOException, ArchiveException {
        try (FileInputStream fis = new FileInputStream(tarGzFilePath);
             GZIPInputStream gzis = new GZIPInputStream(fis);
             TarArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.TAR, gzis)) {

            ArchiveEntry entry;
            String firstFolderName = null;
            while ((entry = ais.getNextEntry()) != null) {
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
                TarArchiveInputStream aisAgain = new ArchiveStreamFactory().createArchiveInputStream(
                        ArchiveStreamFactory.TAR, new GZIPInputStream(new FileInputStream(tarGzFilePath)));
                while ((entry = aisAgain.getNextEntry()) != null) {
                    if (!entry.isDirectory() && entry.getName().startsWith(firstFolderName)) {
                        Path destPath = Paths.get(outputPath, entry.getName().substring(firstFolderName.length()));
                        Files.createDirectories(destPath.getParent());
                        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destPath.toFile()))) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = aisAgain.read(buffer)) > 0) {
                                bos.write(buffer, 0, len);
                            }
                        }
                    }
                }
            }
        }
    }
}