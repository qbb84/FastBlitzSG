package org.rewind.blitzBlitz.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface BlitzPlayer {

    @NotNull
    UUID getUuid();

    @NotNull
    String getDisplayName();

    boolean isAlive();

    boolean isSpectating();

    int getKills();

    int getDeaths();

    @Nullable
    String getKitId();
}
