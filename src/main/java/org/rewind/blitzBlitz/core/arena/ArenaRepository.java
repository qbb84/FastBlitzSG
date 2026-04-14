package org.rewind.blitzBlitz.core.arena;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ArenaRepository {

    void save(@NotNull Arena arena);

    @Nullable
    Arena findByName(@NotNull String name);

    @NotNull
    Collection<Arena> findAll();

    void remove(@NotNull String name);

    @Nullable
    Arena findAvailable();
}
