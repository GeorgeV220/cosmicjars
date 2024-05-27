package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.providers.info.MohistInfo;
import com.georgev22.cosmicjars.utilities.Utils;
import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of Provider for downloading server jars from MohistMC.
 */
public class MohistProvider extends Provider {

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
     * Downloads and returns the path to the server jar.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     * @return The path to the server jar.
     */
    @Override
    public @Nullable String downloadJar(String serverType, String serverImplementation, String serverVersion) {
        String mohistAPI = String.format("https://mohistmc.com/api/v2/projects/%s/%s/builds/", serverImplementation, serverVersion);
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
                String fileName = serverVersion + ".jar";
                String filePath = this.main.getCosmicJarsFolder() + serverType + "/" + serverImplementation + "/" + serverVersion + "/";
                String localPurpurBuild = this.main.getConfig().getString("localBuild." + serverImplementation, "0");
                if (!localPurpurBuild.equals(String.valueOf(mohistInfo.getLatestBuild().getNumber()))) {
                    this.main.getConfig().set("localBuild." + serverImplementation, String.valueOf(mohistInfo.getLatestBuild().getNumber()));
                    this.main.saveConfig();
                } else {
                    File file = new File(filePath + fileName);
                    if (file.exists()) {
                        this.main.getLogger().info("Skipping download of {} jar. File with build number {} already exists: {}", serverImplementation, String.valueOf(mohistInfo.getLatestBuild().getNumber()), filePath + fileName);
                        return filePath + fileName;
                    }
                }
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