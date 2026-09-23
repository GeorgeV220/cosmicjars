package com.georgev22.cosmicjars.pterodactyl.model;

import org.jetbrains.annotations.NotNull;

public final class WebsocketCredentials {
    private final @NotNull String token;
    private final @NotNull String url;

    public WebsocketCredentials(@NotNull String token, @NotNull String url) {
        this.token = token;
        this.url = toWebSocketUrl(url);
    }

    public @NotNull String getToken() {
        return token;
    }

    /**
     * WebSocket endpoint URI (`ws://` or `wss://`).
     */
    public @NotNull String getUrl() {
        return url;
    }

    private static @NotNull String toWebSocketUrl(@NotNull String raw) {
        String trimmed = raw.trim();
        if (trimmed.startsWith("https://")) {
            return "wss://" + trimmed.substring("https://".length());
        }
        //noinspection HttpUrlsUsage
        if (trimmed.startsWith("http://")) {
            //noinspection HttpUrlsUsage
            return "ws://" + trimmed.substring("http://".length());
        }
        return trimmed;
    }
}
