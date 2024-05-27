package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.utilities.Utils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of Provider for downloading server jars from the CentroJars API.
 */
public class CentroJarProvider extends Provider {

    /**
     * Constructs a new CentroJarProvider with the specified server type, implementation, and version.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     */
    public CentroJarProvider(String serverType, String serverImplementation, String serverVersion) {
        super(serverType, serverImplementation, serverVersion);
        this.main.getLogger().warn("CentroJars may provide outdated server jars.");
        this.main.getLogger().warn("It is not possible to save latest build number from CentroJars.");
        this.main.getLogger().warn("Please use another provider if possible or implement your own. (https://github.com/GeorgeV220/CosmicJars/)");
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
        String apiUrl = String.format("https://centrojars.com/api/fetchJar/%s/%s/%s.jar", serverType, serverImplementation, serverVersion);
        this.main.getLogger().debug("Fetching jar: {}", apiUrl);
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = apiUrl.substring(apiUrl.lastIndexOf('/') + 1);

                String filePath = this.main.getCosmicJarsFolder() + serverType + "/" + serverImplementation + "/" + serverVersion + "/";

                return Utils.downloadFile(apiUrl, filePath, fileName);
            } else {
                this.main.getLogger().error("Failed to fetch jar. Response code: {} reason: {}", responseCode, connection.getResponseMessage());
                this.main.getLogger().error("API URL: {}", apiUrl);
            }
        } catch (IOException e) {
            this.main.getLogger().error("Error fetching jar: {}", e.getMessage());
        }
        return null;
    }
}