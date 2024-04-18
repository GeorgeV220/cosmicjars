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
import java.util.Properties;

public class BungeeCordProvider extends Provider {

    private final String API_BASE_URL = "https://ci.md-5.net/job/BungeeCord/api/json";

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

    /**
     * Abstract method to be implemented by subclasses for downloading the server jar.
     *
     * @return The URL of the downloaded server jar.
     */
    @Override
    public String downloadJar() {
        this.main.getLogger().info("Fetching BungeeCord builds from {}", this.API_BASE_URL);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(this.API_BASE_URL).openConnection();

            connection.setRequestMethod("GET");
            int connectionResponseCode = connection.getResponseCode();
            if (connectionResponseCode == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                BungeeCordInfo bungeeCordInfo = gson.fromJson(new InputStreamReader(connection.getInputStream()), BungeeCordInfo.class);

                String latest = String.valueOf(bungeeCordInfo.getLatestBuild().getNumber());
                String apiUrl = bungeeCordInfo.getLatestBuild().getUrl().replace("http", "https") + "artifact/bootstrap/target/BungeeCord.jar";
                this.main.getLogger().debug("Fetching BungeeCord link: {}", apiUrl);
                String fileName = this.getServerImplementation() + ".jar";

                String filePath = this.main.getCosmicJarsFolder() + this.getServerType() + "/" + this.getServerImplementation() + "/";
                Properties properties = this.main.getProperties();
                String localPurpurBuild = properties.getProperty("localBuild.bungeeCord", "0");
                if (!localPurpurBuild.equals(String.valueOf(bungeeCordInfo.getLatestBuild().getNumber()))) {
                    properties.setProperty("localBuild.bungeeCord", String.valueOf(bungeeCordInfo.getLatestBuild().getNumber()));
                    this.main.saveProperties(properties);
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
