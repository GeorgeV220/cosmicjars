package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.utilities.Utils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PandaSpigotProvider extends Provider {

    private static final String ZIP_URL = "https://nightly.link/hpfxd/PandaSpigot/workflows/build/master/Server%20JAR.zip";

    /**
     * Constructs a new Provider with the specified server type, implementation, and version.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     */
    public PandaSpigotProvider(String serverType, String serverImplementation, String serverVersion) {
        super(serverType, serverImplementation, serverVersion);
    }

    /**
     * Downloads and returns the path to the server jar.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     * @return The path to the server jar.
     */
    @Override
    public @Nullable String downloadJar(String serverType, String serverImplementation, String serverVersion) {
        this.main.getLogger().debug("Downloading ZIP file from: {}", ZIP_URL);
        File path = new File(this.main.getCosmicJarsFolder() + serverType + "/" + serverImplementation + "/" + serverVersion + "/");
        String fileName = serverVersion + ".jar";

        try {
            URL url = new URL(ZIP_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                this.main.getLogger().info("Download successful, extracting ZIP...");
                try (ZipInputStream zis = new ZipInputStream(connection.getInputStream())) {
                    ZipEntry zipEntry;
                    while ((zipEntry = zis.getNextEntry()) != null) {
                        if (zipEntry.getName().endsWith(".jar")) {
                            if (!path.exists()) {
                                if (path.mkdirs()) {
                                    this.main.getLogger().info("Created directory: {}", path.getPath());
                                }
                            }
                            File jarFile = new File(path, fileName);
                            if (Utils.copyInputStreamToFile(zis, jarFile)) {
                                this.main.getLogger().info("Extracted JAR file to: {}", jarFile.getPath());
                                return jarFile.getPath();
                            }
                        }
                        zis.closeEntry();
                    }
                }
            } else {
                this.main.getLogger().error("Failed to download ZIP file. Response code: {} reason: {}", responseCode, connection.getResponseMessage());
            }
        } catch (IOException e) {
            this.main.getLogger().error("Error downloading or extracting ZIP file: {}", e.getMessage());
        }
        return null;
    }
}
