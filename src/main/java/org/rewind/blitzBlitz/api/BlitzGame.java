package org.rewind.blitzBlitz.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface BlitzGame {

    @NotNull
    String getArenaName();

    @NotNull
    String getStateName();

    int getAliveCount();

    @NotNull
    Collection<? extends BlitzPlayer> getBlitzPlayers();

    @Nullable
    BlitzPlayer getBlitzPlayer(@NotNull UUID uuid);
}
