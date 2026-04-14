package org.rewind.blitzBlitz.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.game.Game;

import java.util.Collection;
import java.util.UUID;

public final class BlitzAPI {

    private static BlitzPlugin plugin;

    public static void init(@NotNull BlitzPlugin instance) {
        plugin = instance;
    }

    @NotNull
    public static BlitzPlugin getPlugin() {
        if (plugin == null) throw new IllegalStateException("BlitzAPI not initialized");
        return plugin;
    }

    @Nullable
    public static Game getGameByArena(@NotNull String arenaName) {
        return plugin.getActiveGames().get(arenaName.toLowerCase());
    }

    @Nullable
    public static Game getGameByPlayer(@NotNull UUID playerId) {
        for (Game game : plugin.getActiveGames().values()) {
            if (game.getPlayer(playerId) != null) {
                return game;
            }
        }
        return null;
    }

    @NotNull
    public static Collection<Game> getActiveGames() {
        return plugin.getActiveGames().values();
    }
}
