package com.georgev22.cosmicjars.providers.implementations;

import com.georgev22.cosmicjars.providers.Provider;
import com.georgev22.cosmicjars.providers.info.FabricInfo;
import com.georgev22.cosmicjars.utilities.Utils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FabricProvider extends Provider {

    /**
     * Constructs a new Provider with the specified server type, implementation, and version.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     */
    public FabricProvider(String serverType, String serverImplementation, String serverVersion) {
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
        String API_URL = "https://meta.fabricmc.net/v2/versions/loader/%s/%s/%s/server/jar";
        try {
            String latestStableLoader = this.getStableLoaders().get(0).getVersion();
            String latestStableInstaller = this.getStableInstallers().get(0).getVersion();
            String url = String.format(API_URL, serverVersion, latestStableLoader, latestStableInstaller);
            this.main.getLogger().debug("Fetching jar: {}", url);
            URL apiURL = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) apiURL.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String fileName = "fabric-loader-" + serverVersion + "-" + latestStableLoader + "-" + latestStableInstaller + ".jar";
                String filePath = this.main.getCosmicJarsFolder() + serverType + "/" + serverImplementation + "/" + serverVersion + "/";

                return Utils.downloadFile(url, filePath, fileName);
            } else {
                this.main.getLogger().error("Failed to fetch jar. Response code: {} reason: {}", responseCode, connection.getResponseMessage());
                this.main.getLogger().error("API URL: {}", apiURL);
            }
        } catch (IOException e) {
            this.main.getLogger().error("Error fetching jar: {}", e.getMessage());
        }
        return null;
    }

    private @NotNull List<FabricInfo.FabricVersionInfo> getStableVersions() throws IOException {
        URL url = new URL("https://meta.fabricmc.net/v2/versions/game");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        Gson gson = new Gson();
        Type type = new TypeToken<List<FabricInfo.FabricVersionInfo>>() {
        }.getType();
        String json = new String(connection.getInputStream().readAllBytes());
        List<FabricInfo.FabricVersionInfo> versions = gson.fromJson(json, type);

        List<FabricInfo.FabricVersionInfo> stableVersions = new ArrayList<>();
        for (FabricInfo.FabricVersionInfo version : versions) {
            if (version.isStable()) {
                stableVersions.add(version);
            }
        }

        return stableVersions;
    }

    private @NotNull List<FabricInfo.FabricInstallerInfo> getStableInstallers() throws IOException {
        URL url = new URL("https://meta.fabricmc.net/v2/versions/installer");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        Gson gson = new Gson();
        Type type = new TypeToken<List<FabricInfo.FabricInstallerInfo>>() {
        }.getType();
        String json = new String(connection.getInputStream().readAllBytes());
        List<FabricInfo.FabricInstallerInfo> installers = gson.fromJson(json, type);

        List<FabricInfo.FabricInstallerInfo> stableInstallers = new ArrayList<>();
        for (FabricInfo.FabricInstallerInfo installer : installers) {
            if (installer.isStable()) {
                stableInstallers.add(installer);
            }
        }

        return stableInstallers;
    }

    private @NotNull List<FabricInfo.FabricLoaderInfo> getStableLoaders() throws IOException {
        URL url = new URL("https://meta.fabricmc.net/v2/versions/loader");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        Gson gson = new Gson();
        Type type = new TypeToken<List<FabricInfo.FabricLoaderInfo>>() {
        }.getType();
        String json = new String(connection.getInputStream().readAllBytes());
        List<FabricInfo.FabricLoaderInfo> loaders = gson.fromJson(json, type);

        List<FabricInfo.FabricLoaderInfo> stableLoaders = new ArrayList<>();
        for (FabricInfo.FabricLoaderInfo loader : loaders) {
            if (loader.isStable()) {
                stableLoaders.add(loader);
            }
        }

        return stableLoaders;
    }
}
