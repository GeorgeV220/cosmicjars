package com.georgev22.cosmicjars.pterodactyl;

import com.georgev22.cosmicjars.pterodactyl.model.PterodactylServer;
import com.georgev22.cosmicjars.pterodactyl.model.WebsocketCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pterodactyl Client API REST helper (panel client API shape used by modern panels).
 */
public final class PterodactylClient {

    private static final int PER_PAGE = 50;

    private final @NotNull PterodactylSession session;

    public PterodactylClient(@NotNull PterodactylSession session) {
        this.session = session;
    }

    public @NotNull List<PterodactylServer> listServers() throws IOException {
        Map<String, PterodactylServer> servers = new LinkedHashMap<>();
        listServers(servers, false);

        try {
            listServers(servers, true);
        } catch (IOException ignored) {
        }

        return new ArrayList<>(servers.values());
    }

    private void listServers(
            @NotNull Map<String, PterodactylServer> servers,
            boolean other
    ) throws IOException {
        int page = 1;

        while (true) {
            String url = session.getPanelUrl()
                    + "/api/client/servers"
                    + "?page=" + page
                    + "&per_page=" + PER_PAGE
                    + "&other=" + other;

            JsonObject root = getJson(url);
            JsonObject serversObj = root.has("servers")
                    ? root.getAsJsonObject("servers")
                    : null;

            if (serversObj == null) {
                throw new IOException(
                        "Unexpected list response: missing \"servers\" object"
                );
            }

            JsonArray data = serversObj.getAsJsonArray("data");
            if (data == null || data.isEmpty()) {
                break;
            }

            for (JsonElement element : data) {
                JsonObject server = element.getAsJsonObject();
                String uuid = requiredString(server, "uuid");
                String uuidShort = server.has("uuid_short") && !server.get("uuid_short").isJsonNull()
                        ? server.get("uuid_short").getAsString()
                        : uuid;
                String name = server.has("name") && !server.get("name").isJsonNull()
                        ? server.get("name").getAsString()
                        : uuidShort;
                String description = server.has("description") && !server.get("description").isJsonNull()
                        ? server.get("description").getAsString()
                        : "";
                String status = server.has("status") && !server.get("status").isJsonNull()
                        ? server.get("status").getAsString()
                        : null;
                boolean suspended = server.has("is_suspended") && server.get("is_suspended").getAsBoolean();
                servers.put(uuid, new PterodactylServer(uuid, uuidShort, name, description, status, suspended));
            }

            long total = serversObj.has("total")
                    && !serversObj.get("total").isJsonNull()
                    ? serversObj.get("total").getAsLong()
                    : -1;

            if (data.size() < PER_PAGE) {
                break;
            }

            if (total >= 0 && (long) page * PER_PAGE >= total) {
                break;
            }

            page++;
        }
    }

    public void sendPower(@NotNull String serverId, @NotNull String action) throws IOException {
        String url = session.getPanelUrl() + "/api/client/servers/" + serverId + "/power";
        JsonObject body = new JsonObject();
        body.addProperty("action", action);
        postJson(url, body);
    }

    public @NotNull WebsocketCredentials getWebsocketCredentials(@NotNull String serverId) throws IOException {
        String endpoint = session.getPanelUrl() + "/api/client/servers/" + serverId + "/websocket";
        JsonObject root = getJson(endpoint);
        // Panel returns { "token", "url" } (flat). Also accept nested data / legacy "socket".
        JsonObject data = root.has("data") && root.get("data").isJsonObject()
                ? root.getAsJsonObject("data")
                : root;
        if (!data.has("token") || data.get("token").isJsonNull()) {
            throw new IOException("Missing websocket token in response");
        }
        String wsUrl = null;
        if (data.has("url") && !data.get("url").isJsonNull()) {
            wsUrl = data.get("url").getAsString();
        } else if (data.has("socket") && !data.get("socket").isJsonNull()) {
            wsUrl = data.get("socket").getAsString();
        }
        if (wsUrl == null || wsUrl.isBlank()) {
            throw new IOException("Missing websocket url in response");
        }
        return new WebsocketCredentials(data.get("token").getAsString(), wsUrl);
    }

    private static @NotNull String requiredString(@NotNull JsonObject object, @NotNull String key) throws IOException {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            throw new IOException("Server object missing required field: " + key);
        }
        return object.get(key).getAsString();
    }

    private @NotNull JsonObject getJson(@NotNull String urlString) throws IOException {
        HttpURLConnection connection = open(urlString, "GET");
        try {
            int code = connection.getResponseCode();
            InputStream stream = code >= 200 && code < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            String body = readFully(stream);
            if (code < 200 || code >= 300) {
                throw new IOException("HTTP " + code + ": " + body);
            }
            return JsonParser.parseString(body).getAsJsonObject();
        } finally {
            connection.disconnect();
        }
    }

    private void postJson(@NotNull String urlString, @NotNull JsonObject body) throws IOException {
        HttpURLConnection connection = open(urlString, "POST");
        connection.setDoOutput(true);
        byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
        try (OutputStream out = connection.getOutputStream()) {
            out.write(bytes);
        }
        try {
            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                String err = readFully(connection.getErrorStream());
                throw new IOException("HTTP " + code + ": " + err);
            }
        } finally {
            connection.disconnect();
        }
    }

    private @NotNull HttpURLConnection open(@NotNull String urlString, @NotNull String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(15_000);
        connection.setReadTimeout(30_000);
        connection.setRequestProperty("Authorization", "Bearer " + session.getApiKey());
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    private static @NotNull String readFully(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[4096];
            int n;
            while ((n = reader.read(buf)) != -1) {
                sb.append(buf, 0, n);
            }
            return sb.toString();
        }
    }
}
