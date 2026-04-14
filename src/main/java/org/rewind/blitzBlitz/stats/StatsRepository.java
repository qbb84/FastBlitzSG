package org.rewind.blitzBlitz.stats;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.core.player.GameStats;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StatsRepository {

    void saveStats(@NotNull UUID uuid, @NotNull GameStats stats);

    @Nullable
    GameStats loadStats(@NotNull UUID uuid);

    @NotNull
    List<Map.Entry<UUID, GameStats>> findTopPlayers(int limit, @NotNull StatType type);
}
