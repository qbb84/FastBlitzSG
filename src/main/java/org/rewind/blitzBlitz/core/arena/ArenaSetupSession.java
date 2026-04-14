package org.rewind.blitzBlitz.core.arena;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.core.chest.ChestTier;

import java.util.UUID;

public final class ArenaSetupSession {

    private final UUID playerId;
    private final ArenaBuilder builder;

    public ArenaSetupSession(@NotNull UUID playerId, @NotNull String arenaName, @NotNull String worldName) {
        this.playerId = playerId;
        this.builder = new ArenaBuilder().name(arenaName).world(worldName);
    }

    @NotNull
    public UUID getPlayerId() { return playerId; }

    @NotNull
    public ArenaBuilder getBuilder() { return builder; }

    public void addSpawn(@NotNull Location location) {
        builder.addSpawn(location);
    }

    public void setCentre(@NotNull Location location) {
        builder.centre(location);
    }

    public void setDeathmatchCentre(@NotNull Location location) {
        builder.deathmatchCentre(location);
    }

    public void addChest(@NotNull Location location, @NotNull ChestTier tier) {
        builder.addChest(location, tier);
    }

    @NotNull
    public Arena build() {
        return builder.build();
    }
}
