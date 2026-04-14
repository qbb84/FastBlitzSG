package org.rewind.blitzBlitz.core.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface PlayerRepository {

    void save(@NotNull SGPlayer player);

    @Nullable
    SGPlayer findByUuid(@NotNull UUID uuid);

    @NotNull
    Collection<SGPlayer> findAll();

    void remove(@NotNull UUID uuid);

    boolean exists(@NotNull UUID uuid);
}
