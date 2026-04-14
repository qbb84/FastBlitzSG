package org.rewind.blitzBlitz.core.arena;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.chest.ChestTier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ArenaBuilder {

    private String name;
    private String worldName;
    private final List<Location> spawns = new ArrayList<>();
    private Location centre;
    private Location deathmatchCentre;
    private double deathmatchRadius = 50.0;
    private final java.util.LinkedHashMap<Location, ChestTier> chests = new java.util.LinkedHashMap<>();

    @NotNull
    public ArenaBuilder name(@NotNull String name) {
        this.name = name;
        return this;
    }

    @NotNull
    public ArenaBuilder world(@NotNull String worldName) {
        this.worldName = worldName;
        return this;
    }

    @NotNull
    public ArenaBuilder addSpawn(@NotNull Location location) {
        this.spawns.add(location);
        return this;
    }

    @NotNull
    public ArenaBuilder centre(@NotNull Location centre) {
        this.centre = centre;
        return this;
    }

    @NotNull
    public ArenaBuilder deathmatchCentre(@NotNull Location deathmatchCentre) {
        this.deathmatchCentre = deathmatchCentre;
        return this;
    }

    @NotNull
    public ArenaBuilder deathmatchRadius(double radius) {
        this.deathmatchRadius = radius;
        return this;
    }

    @NotNull
    public ArenaBuilder addChest(@NotNull Location location, @NotNull ChestTier tier) {
        this.chests.put(location, tier);
        return this;
    }

    @NotNull
    public Arena build() {
        if (name == null || worldName == null) {
            throw new IllegalStateException("Arena name and world are required");
        }
        Arena arena = new Arena(name, worldName);
        spawns.forEach(arena::addSpawnPedestal);
        if (centre != null) arena.setCentre(centre);
        if (deathmatchCentre != null) arena.setDeathmatchCentre(deathmatchCentre);
        arena.setDeathmatchRadius(deathmatchRadius);
        chests.forEach(arena::addChest);
        return arena;
    }
}
