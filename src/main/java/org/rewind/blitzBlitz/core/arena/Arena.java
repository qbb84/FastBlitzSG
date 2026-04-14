package org.rewind.blitzBlitz.core.arena;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.core.chest.ChestTier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Arena {

    private final String name;
    private final String worldName;
    private final List<Location> spawnPedestals;
    private final Map<Location, ChestTier> chestLocations;
    private Location centre;
    private Location deathmatchCentre;
    private double deathmatchRadius;
    private ArenaState state;

    public Arena(@NotNull String name, @NotNull String worldName) {
        this.name = name;
        this.worldName = worldName;
        this.spawnPedestals = new ArrayList<>();
        this.chestLocations = new HashMap<>();
        this.state = ArenaState.IDLE;
        this.deathmatchRadius = 50.0;
    }

    @NotNull
    public String getName() { return name; }

    @NotNull
    public String getWorldName() { return worldName; }

    @NotNull
    public List<Location> getSpawnPedestals() { return spawnPedestals; }

    @NotNull
    public Map<Location, ChestTier> getChestLocations() { return chestLocations; }

    @Nullable
    public Location getCentre() { return centre; }

    public void setCentre(@NotNull Location centre) { this.centre = centre; }

    @Nullable
    public Location getDeathmatchCentre() { return deathmatchCentre; }

    public void setDeathmatchCentre(@NotNull Location deathmatchCentre) { this.deathmatchCentre = deathmatchCentre; }

    public double getDeathmatchRadius() { return deathmatchRadius; }

    public void setDeathmatchRadius(double deathmatchRadius) { this.deathmatchRadius = deathmatchRadius; }

    @NotNull
    public ArenaState getState() { return state; }

    public void setState(@NotNull ArenaState state) { this.state = state; }

    public int getMaxPlayers() { return spawnPedestals.size(); }

    public void addSpawnPedestal(@NotNull Location location) { spawnPedestals.add(location); }

    public void setSpawnPedestal(int index, @NotNull Location location) {
        while (spawnPedestals.size() <= index) {
            spawnPedestals.add(null);
        }
        spawnPedestals.set(index, location);
    }

    public void addChest(@NotNull Location location, @NotNull ChestTier tier) {
        chestLocations.put(location, tier);
    }

    public boolean isAvailable() {
        return state == ArenaState.IDLE;
    }
}
