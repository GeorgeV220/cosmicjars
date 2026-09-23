package com.georgev22.cosmicjars.pterodactyl;

import com.georgev22.cosmicjars.CosmicJars;
import com.georgev22.cosmicjars.pterodactyl.model.WebsocketCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

/**
 * Live console connection to a Pterodactyl Wings WebSocket.
 */
public final class PterodactylWebSocket implements WebSocket.Listener {

    public interface Listener {
        void onConsoleOutput(@NotNull String line);

        void onStatus(@NotNull String status);

        void onDaemonMessage(@NotNull String message);

        void onError(@NotNull String message);

        void onClosed();
    }

    private final @NotNull PterodactylClient client;
    private final @NotNull String serverId;
    private final @NotNull String panelOrigin;
    private final @NotNull Listener listener;

    private final Object lock = new Object();
    private @Nullable WebSocket webSocket;
    private final StringBuilder textBuffer = new StringBuilder();
    private volatile boolean intentionalClose;

    public PterodactylWebSocket(
            @NotNull PterodactylClient client,
            @NotNull String serverId,
            @NotNull String panelOrigin,
            @NotNull Listener listener
    ) {
        this.client = client;
        this.serverId = serverId;
        this.panelOrigin = panelOrigin;
        this.listener = listener;
    }

    public void connect() {
        intentionalClose = false;
        CompletableFuture.runAsync(() -> {
            try {
                WebsocketCredentials credentials = client.getWebsocketCredentials(serverId);
                openSocket(credentials);
            } catch (Exception e) {
                listener.onError("Failed to connect: " + e.getMessage());
                CosmicJars.getInstance().getLogger().error("Pterodactyl websocket connect failed", e);
            }
        });
    }

    private void openSocket(@NotNull WebsocketCredentials credentials) {
        HttpClient httpClient = HttpClient.newHttpClient();
        httpClient.newWebSocketBuilder()
                .header("Origin", panelOrigin)
                .buildAsync(URI.create(credentials.getUrl()), this)
                .whenComplete((ws, error) -> {
                    if (error != null) {
                        listener.onError("WebSocket handshake failed: " + error.getMessage());
                        CosmicJars.getInstance().getLogger().error("Pterodactyl websocket handshake failed", error);
                        return;
                    }
                    synchronized (lock) {
                        this.webSocket = ws;
                    }
                    sendEvent(ws, "auth", credentials.getToken());
                    sendEvent(ws, "send logs");
                });
    }

    public void sendCommand(@NotNull String command) {
        WebSocket ws = currentSocket();
        if (ws != null) {
            sendEvent(ws, "send command", command);
        }
    }

    public void setState(@NotNull String signal) {
        WebSocket ws = currentSocket();
        if (ws != null) {
            sendEvent(ws, "set state", signal);
        }
    }

    public void close() {
        intentionalClose = true;
        WebSocket ws;
        synchronized (lock) {
            ws = webSocket;
            webSocket = null;
        }
        if (ws != null) {
            try {
                ws.sendClose(WebSocket.NORMAL_CLOSURE, "closed").join();
            } catch (Exception ignored) {
            }
        }
    }

    private @Nullable WebSocket currentSocket() {
        synchronized (lock) {
            return webSocket;
        }
    }

    private void refreshToken() {
        CompletableFuture.runAsync(() -> {
            try {
                WebsocketCredentials credentials = client.getWebsocketCredentials(serverId);
                WebSocket ws = currentSocket();
                if (ws != null) {
                    sendEvent(ws, "auth", credentials.getToken());
                }
            } catch (Exception e) {
                listener.onError("Token refresh failed: " + e.getMessage());
            }
        });
    }

    private static void sendEvent(@NotNull WebSocket ws, @NotNull String event, String @NotNull ... args) {
        JsonObject message = new JsonObject();
        message.addProperty("event", event);
        JsonArray argsArray = new JsonArray();
        for (String arg : args) {
            argsArray.add(arg);
        }
        message.add("args", argsArray);
        ws.sendText(message.toString(), true);
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        textBuffer.append(data);
        if (last) {
            String payload = textBuffer.toString();
            textBuffer.setLength(0);
            handleMessage(payload);
        }
        webSocket.request(1);
        return null;
    }

    private void handleMessage(@NotNull String payload) {
        try {
            JsonObject message = JsonParser.parseString(payload).getAsJsonObject();
            if (!message.has("event")) {
                return;
            }
            String event = message.get("event").getAsString();
            JsonArray args = message.has("args") && message.get("args").isJsonArray()
                    ? message.getAsJsonArray("args")
                    : new JsonArray();
            String firstArg = args.size() > 0 && !args.get(0).isJsonNull() ? args.get(0).getAsString() : "";

            switch (event) {
                case "console output", "install output", "daemon message" -> {
                    if ("daemon message".equals(event)) {
                        listener.onDaemonMessage(firstArg);
                    }
                    listener.onConsoleOutput(firstArg.endsWith("\n") ? firstArg : firstArg + "\n");
                }
                case "status" -> listener.onStatus(firstArg);
                case "token expiring", "token expired" -> refreshToken();
                case "jwt error", "daemon error" -> listener.onError(firstArg.isBlank() ? event : firstArg);
                default -> {
                }
            }
        } catch (Exception e) {
            listener.onError("Failed to parse websocket message: " + e.getMessage());
        }
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        synchronized (lock) {
            this.webSocket = null;
        }
        if (!intentionalClose) {
            listener.onError("WebSocket closed (" + statusCode + "): " + reason);
        }
        listener.onClosed();
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        listener.onError("WebSocket error: " + error.getMessage());
        CosmicJars.getInstance().getLogger().error("Pterodactyl websocket error", error);
    }
}
