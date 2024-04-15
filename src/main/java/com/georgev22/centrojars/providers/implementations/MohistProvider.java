package com.georgev22.centrojars.providers.implementations;

import com.georgev22.centrojars.providers.Provider;
import com.georgev22.centrojars.providers.info.MohistInfo;
import com.georgev22.centrojars.utilities.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of Provider for downloading server jars from MohistMC.
 */
public class MohistProvider extends Provider {

    /**
     * Base URL of the MohistMC API for retrieving builds.
     */
    private final String API_BASE_URL = "https://mohistmc.com/api/v2/projects/%s/%s/builds/";

    /**
     * Constructs a new MohistProvider with the specified server type, implementation, and version.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     */
    public MohistProvider(String serverType, String serverImplementation, String serverVersion) {
        super(serverType, serverImplementation, serverVersion);
    }

    /**
     * Downloads the server jar from MohistMC.
     *
     * @return The URL of the downloaded server jar, or null if the download fails.
     */
    @Override
    public String downloadJar() {
        String mohistAPI = String.format(API_BASE_URL, this.getServerImplementation(), this.getServerVersion());
        this.main.getLogger().debug("Fetching Mohist link: {}", mohistAPI);
        try {
            URL mohistBuildsURL = new URL(mohistAPI);
            HttpURLConnection connection = (HttpURLConnection) mohistBuildsURL.openConnection();
            connection.setRequestMethod("GET");
            int connectionResponseCode = connection.getResponseCode();
            if (connectionResponseCode == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                MohistInfo mohistInfo = gson.fromJson(new InputStreamReader(connection.getInputStream()), MohistInfo.class);
                String apiUrl = mohistInfo.getLatestBuild().getUrl();
                String fileName = this.getServerVersion() + ".jar";
                String filePath = this.main.getCentroJarsFolder() + this.getServerType() + "/" + this.getServerImplementation() + "/" + this.getServerVersion() + "/";
                return Utils.downloadFile(apiUrl, filePath, fileName);
            } else {
                this.main.getLogger().error("Failed to fetch Mohist link. Response code: {} reason: {}", connectionResponseCode, connection.getResponseMessage());
            }
        } catch (IOException e) {
            this.main.getLogger().error("Error fetching jar: {}", e.getMessage());
        }
        return null;
    }

}