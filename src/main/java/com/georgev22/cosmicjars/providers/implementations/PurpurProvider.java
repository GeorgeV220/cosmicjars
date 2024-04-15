package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.providers.info.PurpurInfo;
import com.georgev22.cosmicjars.utilities.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of Provider for downloading server jars from PurpurMC.
 */
public class PurpurProvider extends Provider {

    /**
     * Base URL of the PurpurMC API for retrieving versions.
     */
    private final String API_BASE_URL = "https://api.purpurmc.org/v2/purpur/";

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
     * Downloads the server jar from PurpurMC.
     *
     * @return The URL of the downloaded server jar, or null if the download fails.
     */
    @Override
    public String downloadJar() {
        String purpurAPI = API_BASE_URL + this.getServerVersion() + "/";
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
                String fileName = this.getServerVersion() + ".jar";
                String filePath = this.main.getCosmicJarsFolder() + this.getServerType() + "/" + this.getServerImplementation() + "/" + this.getServerVersion() + "/";
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