package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.providers.info.BungeeCordInfo;
import com.georgev22.cosmicjars.utilities.Utils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BungeeCordProvider extends Provider {

    /**
     * Constructs a new Provider with the specified server type, implementation, and version.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     */
    public BungeeCordProvider(String serverType, String serverImplementation, String serverVersion) {
        super(serverType, serverImplementation, serverVersion);
    }

    @Override
    public String downloadJar(String serverType, String serverImplementation, String serverVersion) {
        String API_BASE_URL = "https://ci.md-5.net/job/BungeeCord/api/json";
        this.main.getLogger().info("Fetching BungeeCord builds from {}", API_BASE_URL);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_BASE_URL).openConnection();

            connection.setRequestMethod("GET");
            int connectionResponseCode = connection.getResponseCode();
            if (connectionResponseCode == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                BungeeCordInfo bungeeCordInfo = gson.fromJson(new InputStreamReader(connection.getInputStream()), BungeeCordInfo.class);

                String latest = String.valueOf(bungeeCordInfo.getLatestBuild().getNumber());
                String apiUrl = bungeeCordInfo.getLatestBuild().getUrl().replace("http", "https") + "artifact/bootstrap/target/BungeeCord.jar";
                this.main.getLogger().debug("Fetching BungeeCord link: {}", apiUrl);
                String fileName = serverImplementation + ".jar";

                String filePath = this.main.getCosmicJarsFolder() + serverType + "/" + serverImplementation + "/";
                String localPurpurBuild = this.main.getConfig().getString("localBuild.bungeeCord", "0");
                if (!localPurpurBuild.equals(String.valueOf(bungeeCordInfo.getLatestBuild().getNumber()))) {
                    this.main.getConfig().set("localBuild.bungeeCord", String.valueOf(bungeeCordInfo.getLatestBuild().getNumber()));
                    this.main.saveConfig();
                } else {
                    File file = new File(filePath + fileName);
                    if (file.exists()) {
                        this.main.getLogger().info(
                                "Skipping download of {} jar. File with build number {} already exists: {}",
                                "BungeeCord",
                                String.valueOf(bungeeCordInfo.getLatestBuild().getNumber()),
                                filePath + fileName
                        );
                        return filePath + fileName;
                    }
                }
                return Utils.downloadFile(apiUrl, filePath, fileName);
            }
        } catch (IOException e) {
            this.main.getLogger().error("Error fetching jar: {}", e.getMessage());
        }

        return null;
    }
}
