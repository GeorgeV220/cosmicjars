package com.georgev22.cosmicjars.pterodactyl;

import com.georgev22.cosmicjars.CosmicJars;
import com.georgev22.cosmicjars.utilities.SecretCrypto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Holds unlocked Pterodactyl Client API credentials for the current GUI session.
 * The API key is never written to disk in plaintext.
 */
public final class PterodactylSession {

    private static final PterodactylSession INSTANCE = new PterodactylSession();

    private final Object lock = new Object();
    private @Nullable String panelUrl;
    private char @Nullable [] apiKey;

    private PterodactylSession() {
    }

    public static PterodactylSession getInstance() {
        return INSTANCE;
    }

    public boolean hasStoredCredentials() {
        CosmicJars main = CosmicJars.getInstance();
        if (main == null || main.getConfig() == null) {
            return false;
        }
        String url = main.getConfig().getString("pterodactyl.url", "");
        String encrypted = main.getConfig().getString("pterodactyl.encryptedApiKey", "");
        return url != null && !url.isBlank() && encrypted != null && !encrypted.isBlank();
    }

    public boolean isUnlocked() {
        synchronized (lock) {
            return panelUrl != null && apiKey != null && apiKey.length > 0;
        }
    }

    public void unlock(char @NotNull [] password) throws GeneralSecurityException {
        CosmicJars main = CosmicJars.getInstance();
        String url = main.getConfig().getString("pterodactyl.url", "");
        String encrypted = main.getConfig().getString("pterodactyl.encryptedApiKey", "");
        if (url == null || url.isBlank() || encrypted == null || encrypted.isBlank()) {
            throw new GeneralSecurityException("No stored Pterodactyl credentials");
        }
        String decrypted = SecretCrypto.decrypt(encrypted, password);
        synchronized (lock) {
            clearApiKeyUnlocked();
            this.panelUrl = normalizeUrl(url);
            this.apiKey = decrypted.toCharArray();
        }
    }

    public void saveAndUnlock(
            @NotNull String panelUrl,
            @NotNull String apiKeyPlaintext,
            char @NotNull [] password
    ) throws GeneralSecurityException {
        String normalized = normalizeUrl(panelUrl);
        String encrypted = SecretCrypto.encrypt(apiKeyPlaintext, password);
        CosmicJars main = CosmicJars.getInstance();
        main.getConfig().set("pterodactyl.url", normalized);
        main.getConfig().set("pterodactyl.encryptedApiKey", encrypted);
        main.saveConfig();
        synchronized (lock) {
            clearApiKeyUnlocked();
            this.panelUrl = normalized;
            this.apiKey = apiKeyPlaintext.toCharArray();
        }
    }

    public void clearStoredCredentials() {
        CosmicJars main = CosmicJars.getInstance();
        main.getConfig().set("pterodactyl.url", "");
        main.getConfig().set("pterodactyl.encryptedApiKey", "");
        main.saveConfig();
        lockSession();
    }

    public void lockSession() {
        synchronized (lock) {
            clearApiKeyUnlocked();
            this.panelUrl = null;
        }
    }

    public @NotNull String getPanelUrl() {
        synchronized (lock) {
            if (panelUrl == null) {
                throw new IllegalStateException("Pterodactyl session is locked");
            }
            return panelUrl;
        }
    }

    public @NotNull String getApiKey() {
        synchronized (lock) {
            if (apiKey == null) {
                throw new IllegalStateException("Pterodactyl session is locked");
            }
            return new String(apiKey);
        }
    }

    private void clearApiKeyUnlocked() {
        if (apiKey != null) {
            Arrays.fill(apiKey, '\0');
            apiKey = null;
        }
    }

    private static @NotNull String normalizeUrl(@NotNull String url) {
        String trimmed = url.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
