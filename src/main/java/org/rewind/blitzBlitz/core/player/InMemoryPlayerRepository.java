package org.rewind.blitzBlitz.core.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryPlayerRepository implements PlayerRepository {

    private final Map<UUID, SGPlayer> players = new ConcurrentHashMap<>();

    @Override
    public void save(@NotNull SGPlayer player) {
        players.put(player.getUuid(), player);
    }

    @Override
    @Nullable
    public SGPlayer findByUuid(@NotNull UUID uuid) {
        return players.get(uuid);
    }

    @Override
    @NotNull
    public Collection<SGPlayer> findAll() {
        return Collections.unmodifiableCollection(players.values());
    }

    @Override
    public void remove(@NotNull UUID uuid) {
        players.remove(uuid);
    }

    @Override
    public boolean exists(@NotNull UUID uuid) {
        return players.containsKey(uuid);
    }
}
