package com.georgev22.cosmicjars.pterodactyl.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PterodactylServer {
    private final @NotNull String uuid;
    private final @NotNull String uuidShort;
    private final @NotNull String name;
    private final @NotNull String description;
    private final @Nullable String status;
    private final boolean isSuspended;

    public PterodactylServer(
            @NotNull String uuid,
            @NotNull String uuidShort,
            @NotNull String name,
            @NotNull String description,
            @Nullable String status,
            boolean isSuspended
    ) {
        this.uuid = uuid;
        this.uuidShort = uuidShort;
        this.name = name;
        this.description = description;
        this.status = status;
        this.isSuspended = isSuspended;
    }

    /**
     * Full server UUID used in API paths.
     */
    public @NotNull String getIdentifier() {
        return uuid;
    }

    public @NotNull String getUuid() {
        return uuid;
    }

    public @NotNull String getUuidShort() {
        return uuidShort;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public @Nullable String getStatus() {
        return status;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    @Override
    public String toString() {
        return name + " (" + uuidShort + ")";
    }
}
