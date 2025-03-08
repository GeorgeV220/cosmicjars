package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.providers.info.MCJarsInfo;
import com.georgev22.cosmicjars.utilities.Utils;
import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class MCJarsProvider extends Provider {
    private static final Set<String> VALID_TYPES = Set.of(
            "VANILLA", "PAPER", "PUFFERFISH", "SPIGOT", "FOLIA", "PURPUR", "WATERFALL",
            "VELOCITY", "FABRIC", "BUNGEECORD", "QUILT", "FORGE", "NEOFORGE", "MOHIST",
            "ARCLIGHT", "SPONGE", "LEAVES", "CANVAS", "ASPAPER", "LEGACY_FABRIC",
            "LOOHP_LIMBO", "NANOLIMBO"
    );

    /**
     * Constructs a new Provider with the specified server type, implementation, and version.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     */
    public MCJarsProvider(String serverType, String serverImplementation, String serverVersion) {
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
        if (!VALID_TYPES.contains(serverImplementation.toUpperCase())) {
            this.main.getLogger().error("Invalid server type: {}", serverImplementation);
            return null;
        }

        String apiUrl = "https://versions.mcjars.app/api/v2/builds/" + serverImplementation.toUpperCase() + "?tracking=none";
        this.main.getLogger().debug("Fetching MCJars API: {}", apiUrl);

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                MCJarsInfo response = gson.fromJson(new InputStreamReader(connection.getInputStream()), MCJarsInfo.class);
                MCJarsInfo.BuildInfo builds = response.getBuilds().get(serverVersion);

                if (builds != null && builds.getLatest() != null) {
                    MCJarsInfo.LatestBuild latestBuild = builds.getLatest();
                    String latestBuildNumber = String.valueOf(latestBuild.getBuildNumber());
                    String localBuildNumber = this.main.getConfig().getString("localBuild." + serverImplementation, "0");

                    if (localBuildNumber.equals(latestBuildNumber)) {
                        this.main.getLogger().info("Skipping download. Latest build {} is already present.", latestBuildNumber);
                        return this.main.getCosmicJarsFolder() + serverType + "/" + serverImplementation + "/" + serverVersion + "/" + serverVersion + ".jar";
                    }

                    this.main.getConfig().set("localBuild." + serverImplementation, latestBuildNumber);
                    this.main.saveConfig();

                    String jarUrl = latestBuild.getJarUrl();
                    String fileName = serverVersion + ".jar";
                    String filePath = this.main.getCosmicJarsFolder() + serverType + "/" + serverImplementation + "/" + serverVersion + "/";

                    return Utils.downloadFile(jarUrl, filePath, fileName);
                } else {
                    this.main.getLogger().error("No builds found for version {}", serverVersion);
                }
            } else {
                this.main.getLogger().error("Failed to fetch data. Response code: {}", connection.getResponseCode());
            }
        } catch (IOException e) {
            this.main.getLogger().error("Error fetching jar: {}", e.getMessage());
        }
        return null;
    }
}
