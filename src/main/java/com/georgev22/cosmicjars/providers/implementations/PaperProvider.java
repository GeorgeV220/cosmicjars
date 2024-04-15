package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.providers.info.PaperInfo;
import com.georgev22.cosmicjars.utilities.Utils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Implementation of Provider for downloading server jars from PaperMC.
 */
public class PaperProvider extends Provider {

    /**
     * Base URL of the PaperMC API for retrieving versions.
     */
    private final String API_BASE_URL = "https://papermc.io/api/v2/projects/%s/versions/";

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
     * Downloads the server jar from PaperMC.
     *
     * @return The URL of the downloaded server jar, or null if the download fails.
     */
    @Override
    public String downloadJar() {
        String paperAPI = String.format(API_BASE_URL, this.getServerImplementation()) + this.getServerVersion() + "/";
        this.main.getLogger().debug("Fetching Paper link: {}", paperAPI);
        try {
            URL paperBuildsURL = new URL(paperAPI);
            HttpURLConnection paperConnection = (HttpURLConnection) paperBuildsURL.openConnection();
            paperConnection.setRequestMethod("GET");
            int paperConnectionResponseCode = paperConnection.getResponseCode();
            if (paperConnectionResponseCode == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                PaperInfo paperInfo = gson.fromJson(new InputStreamReader(paperConnection.getInputStream()), PaperInfo.class);
                String latest = String.valueOf(paperInfo.getLatestBuild());
                String apiUrl = paperAPI + "builds/" + latest + "/downloads/" + this.getServerImplementation() + "-" + this.getServerVersion() + "-" + latest + ".jar";
                String fileName = this.getServerVersion() + ".jar";

                String filePath = this.main.getCosmicJarsFolder() + this.getServerType() + "/" + this.getServerImplementation() + "/" + this.getServerVersion() + "/";
                Properties properties = this.main.getProperties();
                String localPurpurBuild = properties.getProperty("localBuild." + this.getServerImplementation(), "0");
                if (!localPurpurBuild.equals(String.valueOf(paperInfo.getLatestBuild()))) {
                    properties.setProperty("localBuild." + this.getServerImplementation(), String.valueOf(paperInfo.getLatestBuild()));
                    this.main.saveProperties(properties);
                } else {
                    File file = new File(filePath + fileName);
                    if (file.exists()) {
                        this.main.getLogger().info("Skipping download of {} jar. File with build number {} already exists: {}", this.getServerImplementation(), String.valueOf(paperInfo.getLatestBuild()), filePath + fileName);
                        return filePath + fileName;
                    }
                }
                return Utils.downloadFile(apiUrl, filePath, fileName);
            } else {
                this.main.getLogger().error("Failed to fetch Paper link. Response code: {} reason: {}", paperConnectionResponseCode, paperConnection.getResponseMessage());
            }
        } catch (IOException e) {
            this.main.getLogger().error("Error fetching jar: {}", e.getMessage());
        }
        return null;
    }
}