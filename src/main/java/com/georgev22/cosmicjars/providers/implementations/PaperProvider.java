package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.utilities.Utils;
import com.google.gson.*;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of Provider for downloading server jars from PaperMC.
 */
public class PaperProvider extends Provider {

    /**
     * Constructs a new PaperProvider with the specified server type, implementation, and version.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     */
    public PaperProvider(String serverType, String serverImplementation, String serverVersion) {
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
        String paperAPI = String.format(
                "https://fill.papermc.io/v3/projects/%s/versions/%s/builds",
                serverImplementation,
                serverVersion
        );

        this.main.getLogger().debug("Fetching Paper builds: {}", paperAPI);

        HttpURLConnection connection = null;

        try {
            URL url = new URL(paperAPI);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty(
                    "User-Agent",
                    "CosmicJars/1.0"
            );
            connection.setRequestProperty("Accept", "application/json");

            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                this.main.getLogger().error(
                        "Failed to fetch Paper builds. Response code: {} reason: {}",
                        responseCode,
                        connection.getResponseMessage()
                );
                return null;
            }

            Gson gson = new Gson();

            JsonArray builds;

            try (InputStreamReader reader = new InputStreamReader(
                    connection.getInputStream(),
                    StandardCharsets.UTF_8
            )) {
                builds = gson.fromJson(reader, JsonArray.class);
            }

            JsonObject latestStableBuild = null;
            int latestBuildId = -1;

            for (JsonElement element : builds) {
                JsonObject build = element.getAsJsonObject();

                if (!build.has("channel")
                        || !"STABLE".equalsIgnoreCase(build.get("channel").getAsString())) {
                    continue;
                }

                int buildId = build.get("id").getAsInt();

                if (buildId > latestBuildId) {
                    latestBuildId = buildId;
                    latestStableBuild = build;
                }
            }

            if (latestStableBuild == null) {
                this.main.getLogger().error(
                        "No stable {} build found for Minecraft {}",
                        serverImplementation,
                        serverVersion
                );
                return null;
            }

            JsonObject downloads = latestStableBuild.getAsJsonObject("downloads");

            if (downloads == null || !downloads.has("server:default")) {
                this.main.getLogger().error(
                        "Build {} does not contain a server:default download",
                        latestBuildId
                );
                return null;
            }

            JsonObject serverDownload = downloads.getAsJsonObject("server:default");

            if (!serverDownload.has("url")) {
                this.main.getLogger().error(
                        "Build {} does not contain a download URL",
                        latestBuildId
                );
                return null;
            }

            String downloadUrl = serverDownload.get("url").getAsString();

            String fileName = serverVersion + ".jar";
            String filePath = this.main.getCosmicJarsFolder()
                    + serverType + "/"
                    + serverImplementation + "/"
                    + serverVersion + "/";

            String latestBuild = String.valueOf(latestBuildId);
            String localBuild = this.main.getConfig().getString(
                    "localBuild." + serverImplementation,
                    "0"
            );

            if (localBuild.equals(latestBuild)) {
                File file = new File(filePath + fileName);

                if (file.exists()) {
                    this.main.getLogger().info(
                            "Skipping download of {} jar. File with build number {} already exists: {}",
                            serverImplementation,
                            latestBuild,
                            file.getAbsolutePath()
                    );

                    return file.getAbsolutePath();
                }
            }

            this.main.getConfig().set(
                    "localBuild." + serverImplementation,
                    latestBuild
            );
            this.main.saveConfig();

            this.main.getLogger().info(
                    "Downloading {} {} build {}",
                    serverImplementation,
                    serverVersion,
                    latestBuild
            );

            return Utils.downloadFile(downloadUrl, filePath, fileName);
        } catch (IOException | JsonParseException e) {
            this.main.getLogger().error(
                    "Error fetching {} jar: {}",
                    serverImplementation,
                    e.getMessage()
            );
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }
}