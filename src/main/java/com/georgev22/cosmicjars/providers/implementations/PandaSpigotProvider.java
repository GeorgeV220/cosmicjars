package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.providers.info.PandaSpigotInfo;
import com.georgev22.cosmicjars.utilities.Utils;
import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of Provider for downloading PandaSpigot jars.
 */
public class PandaSpigotProvider extends Provider {

    private static final String META_URL = "https://downloads.hpfxd.com/v2/projects/pandaspigot/versions/1.8.8/builds/latest";

    /**
     * Constructs a new PandaSpigotProvider with the specified server type, implementation, and version.
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
        this.main.getLogger().debug("Fetching PandaSpigot metadata: {}", META_URL);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(META_URL).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                this.main.getLogger().error("Failed to fetch metadata. Code: {} reason: {}", responseCode, connection.getResponseMessage());
                return null;
            }

            Gson gson = new Gson();
            PandaSpigotInfo meta = gson.fromJson(new InputStreamReader(connection.getInputStream()), PandaSpigotInfo.class);

            int buildNumber = meta.getBuild();
            String downloadUrl = String.format(
                    "https://downloads.hpfxd.com/v2/projects/pandaspigot/versions/1.8.8/builds/%d/downloads/paperclip",
                    buildNumber
            );

            String filePath = this.main.getCosmicJarsFolder() + serverType + "/" + serverImplementation + "/1.8.8/";
            String fileName =  "1.8.8.jar";
            String configKey = "localBuild." + serverImplementation;
            String localBuild = this.main.getConfig().getString(configKey, "0");

            File jarFile = new File(filePath + fileName);
            if (localBuild.equals(String.valueOf(buildNumber)) && jarFile.exists()) {
                this.main.getLogger().info("Skipping download. Existing JAR for build {} already exists at {}", buildNumber, jarFile.getPath());
                return jarFile.getPath();
            }

            this.main.getLogger().info("New build detected ({}), downloading from {}", buildNumber, downloadUrl);

            File pathDir = new File(filePath);
            if (!pathDir.exists() && pathDir.mkdirs()) {
                this.main.getLogger().info("Created directory: {}", pathDir.getPath());
            }

            Utils.downloadFile(downloadUrl, filePath, fileName);
            this.main.getLogger().info("Downloaded JAR to: {}", jarFile.getPath());

            this.main.getConfig().set(configKey, String.valueOf(buildNumber));
            this.main.saveConfig();

            return jarFile.getPath();

        } catch (IOException e) {
            this.main.getLogger().error("Error while fetching or downloading PandaSpigot: {}", e.getMessage());
        }

        return null;
    }
}
