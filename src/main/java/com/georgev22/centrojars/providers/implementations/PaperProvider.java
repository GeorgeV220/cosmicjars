package com.georgev22.centrojars.providers.implementations;

import com.georgev22.centrojars.providers.Provider;
import com.georgev22.centrojars.providers.info.PaperInfo;
import com.georgev22.centrojars.utilities.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

                String filePath = this.main.getCentroJarsFolder() + this.getServerType() + "/" + this.getServerImplementation() + "/" + this.getServerVersion() + "/";
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