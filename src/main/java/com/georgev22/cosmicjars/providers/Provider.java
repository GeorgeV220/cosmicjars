package com.georgev22.cosmicjars.providers;

import com.georgev22.cosmicjars.Main;

/**
 * Abstract class representing a provider for downloading server jars.
 */
public abstract class Provider {

    /**
     * Main instance of the application.
     */
    protected final Main main = Main.getInstance();

    /**
     * Type of the server (e.g., Bukkit, Spigot).
     */
    private final String serverType;

    /**
     * Name of the server implementation (e.g., CraftBukkit, Paper).
     */
    private final String serverImplementation;

    /**
     * Version of the server.
     */
    private final String serverVersion;

    /**
     * Constructs a new Provider with the specified server type, implementation, and version.
     *
     * @param serverType           Type of the server.
     * @param serverImplementation Name of the server implementation.
     * @param serverVersion        Version of the server.
     */
    public Provider(String serverType, String serverImplementation, String serverVersion) {
        this.serverType = serverType;
        this.serverImplementation = serverImplementation;
        this.serverVersion = serverVersion;
    }

    /**
     * Retrieves the server implementation.
     *
     * @return The server implementation.
     */
    public String getServerImplementation() {
        return this.serverImplementation;
    }

    /**
     * Retrieves the server type.
     *
     * @return The server type.
     */
    public String getServerType() {
        return this.serverType;
    }

    /**
     * Retrieves the server version.
     *
     * @return The server version.
     */
    public String getServerVersion() {
        return this.serverVersion;
    }

    /**
     * Abstract method to be implemented by subclasses for downloading the server jar.
     *
     * @return The URL of the downloaded server jar.
     */
    public abstract String downloadJar();

    /**
     * Retrieves information about the server.
     *
     * @return Information about the server.
     */
    public String getServerInfo() {
        return this.getServerType() + " " + this.getServerImplementation() + " " + this.getServerVersion();
    }

}