package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.providers.info.PurpurInfo;
import com.georgev22.cosmicjars.utilities.Utils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of Provider for downloading server jars from PurpurMC.
 */
public class PurpurProvider extends Provider {

    /**
     * Constructs a new PurpurProvider with the specified server type, implementation, and version.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     */
    public PurpurProvider(String serverType, String serverImplementation, String serverVersion) {
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
    public String downloadJar(String serverType, String serverImplementation, String serverVersion) {
        String purpurAPI = String.format("https://api.purpurmc.org/v2/purpur/%s/", serverVersion);
        this.main.getLogger().debug("Fetching Purpur link: {}", purpurAPI);
        try {
            URL purpurBuildsURL = new URL(purpurAPI);
            HttpURLConnection purpurConnection = (HttpURLConnection) purpurBuildsURL.openConnection();
            purpurConnection.setRequestMethod("GET");
            int purpurConnectionResponseCode = purpurConnection.getResponseCode();
            if (purpurConnectionResponseCode == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                PurpurInfo purpurInfo = gson.fromJson(new InputStreamReader(purpurConnection.getInputStream()), PurpurInfo.class);
                String latest = purpurInfo.builds().latest();
                String apiUrl = purpurAPI + latest + "/download";
                String fileName = serverVersion + ".jar";
                String filePath = this.main.getCosmicJarsFolder() + serverType + "/" + serverImplementation + "/" + serverVersion + "/";
                String localPurpurBuild = this.main.getConfig().getString("localBuild.purpur", "0");
                if (!localPurpurBuild.equals(purpurInfo.builds().latest())) {
                    this.main.getConfig().set("localBuild.purpur", purpurInfo.builds().latest());
                    this.main.saveConfig();
                } else {
                    File file = new File(filePath + fileName);
                    if (file.exists()) {
                        this.main.getLogger().info("Skipping download of {} jar. File with build number {} already exists: {}", serverImplementation, purpurInfo.builds().latest(), filePath + fileName);
                        return filePath + fileName;
                    }
                }
                return Utils.downloadFile(apiUrl, filePath, fileName);
            } else {
                this.main.getLogger().error("Failed to fetch Purpur link. Response code: {} reason: {}", purpurConnectionResponseCode, purpurConnection.getResponseMessage());
            }
        } catch (IOException e) {
            this.main.getLogger().error("Error fetching jar: {}", e.getMessage());
        }
        return null;
    }

}