package org.rewind.blitzBlitz.core.arena;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryArenaRepository implements ArenaRepository {

    private final Map<String, Arena> arenas = new ConcurrentHashMap<>();

    @Override
    public void save(@NotNull Arena arena) {
        arenas.put(arena.getName().toLowerCase(), arena);
    }

    @Override
    @Nullable
    public Arena findByName(@NotNull String name) {
        return arenas.get(name.toLowerCase());
    }

    @Override
    @NotNull
    public Collection<Arena> findAll() {
        return Collections.unmodifiableCollection(arenas.values());
    }

    @Override
    public void remove(@NotNull String name) {
        arenas.remove(name.toLowerCase());
    }

    @Override
    @Nullable
    public Arena findAvailable() {
        return arenas.values().stream()
                .filter(Arena::isAvailable)
                .findFirst()
                .orElse(null);
    }
}
