package org.rewind.blitzBlitz.cosmetics;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.api.events.BlitzGameEndEvent;
import org.rewind.blitzBlitz.api.events.BlitzPlayerEliminatedEvent;
import org.rewind.blitzBlitz.core.event.BlitzEventBus;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.player.SGPlayer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class CosmeticsManager {

    private final BlitzPlugin plugin;
    private final Logger logger;
    private final File cosmeticsDir;

    private final Map<UUID, TrailEffect> activeTrails;
    private final Map<UUID, TrailContext> contextMap;
    private final Map<UUID, Set<TrailEffect>> ownedCosmetics;

    public CosmeticsManager(@NotNull BlitzPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getPlugin().getLogger();
        this.cosmeticsDir = new File(plugin.getPlugin().getDataFolder(), "cosmetics");
        this.activeTrails = new ConcurrentHashMap<>();
        this.contextMap = new ConcurrentHashMap<>();
        this.ownedCosmetics = new ConcurrentHashMap<>();

        if (!cosmeticsDir.exists()) {
            cosmeticsDir.mkdirs();
        }
    }

    public void subscribeEvents(@NotNull BlitzEventBus eventBus) {
        eventBus.subscribe(BlitzPlayerEliminatedEvent.class, this::onPlayerEliminated);
        eventBus.subscribe(BlitzGameEndEvent.class, this::onGameEnd);
    }

    public void onTick() {
        for (Game game : plugin.getActiveGames().values()) {
            GameState state = game.getState();
            if (state != GameState.ACTIVE && state != GameState.DEATHMATCH) continue;

            for (SGPlayer sgPlayer : game.getAlivePlayers()) {
                UUID uuid = sgPlayer.getUuid();
                TrailEffect trail = activeTrails.get(uuid);
                if (trail == null) continue;

                Player player = sgPlayer.getBukkitPlayer();
                if (player == null) continue;

                TrailContext ctx = contextMap.computeIfAbsent(uuid, k -> new TrailContext());
                ctx.update(player.getLocation(), player.isSprinting());
                trail.getRenderer().render(player, ctx);
            }
        }
    }

    public void setActiveTrail(@NotNull UUID playerId, @Nullable TrailEffect effect) {
        if (effect == null) {
            activeTrails.remove(playerId);
            contextMap.remove(playerId);
        } else {
            activeTrails.put(playerId, effect);
            contextMap.put(playerId, new TrailContext());
        }
    }

    @Nullable
    public TrailEffect getActiveTrail(@NotNull UUID playerId) {
        return activeTrails.get(playerId);
    }

    public boolean purchaseTrail(@NotNull UUID playerId, @NotNull TrailEffect effect) {
        Set<TrailEffect> owned = ownedCosmetics.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet());
        if (owned.contains(effect)) return false;
        owned.add(effect);
        savePlayerCosmetics(playerId);
        return true;
    }

    public boolean ownsTrail(@NotNull UUID playerId, @NotNull TrailEffect effect) {
        Set<TrailEffect> owned = ownedCosmetics.get(playerId);
        return owned != null && owned.contains(effect);
    }

    @NotNull
    public Set<TrailEffect> getOwnedTrails(@NotNull UUID playerId) {
        return ownedCosmetics.getOrDefault(playerId, Collections.emptySet());
    }

    public void loadPlayerCosmetics(@NotNull UUID playerId) {
        File file = new File(cosmeticsDir, playerId + ".yml");
        if (!file.exists()) return;

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        List<String> trailNames = cfg.getStringList("owned-trails");
        Set<TrailEffect> owned = ConcurrentHashMap.newKeySet();
        for (String name : trailNames) {
            try {
                owned.add(TrailEffect.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        ownedCosmetics.put(playerId, owned);

        String activeName = cfg.getString("active-trail");
        if (activeName != null) {
            try {
                TrailEffect active = TrailEffect.valueOf(activeName.toUpperCase());
                if (owned.contains(active)) {
                    activeTrails.put(playerId, active);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void savePlayerCosmetics(@NotNull UUID playerId) {
        File file = new File(cosmeticsDir, playerId + ".yml");
        FileConfiguration cfg = new YamlConfiguration();

        Set<TrailEffect> owned = ownedCosmetics.get(playerId);
        if (owned != null) {
            List<String> trailNames = new ArrayList<>();
            for (TrailEffect effect : owned) {
                trailNames.add(effect.name());
            }
            cfg.set("owned-trails", trailNames);
        }

        TrailEffect active = activeTrails.get(playerId);
        if (active != null) {
            cfg.set("active-trail", active.name());
        }

        try {
            cfg.save(file);
        } catch (IOException e) {
            logger.severe("[Cosmetics] Failed to save cosmetics for " + playerId + ": " + e.getMessage());
        }
    }

    public void saveAll() {
        Set<UUID> allPlayers = new HashSet<>();
        allPlayers.addAll(ownedCosmetics.keySet());
        allPlayers.addAll(activeTrails.keySet());
        for (UUID uuid : allPlayers) {
            savePlayerCosmetics(uuid);
        }
    }

    public void clearPlayer(@NotNull UUID playerId) {
        activeTrails.remove(playerId);
        contextMap.remove(playerId);
    }

    private void onPlayerEliminated(@NotNull BlitzPlayerEliminatedEvent event) {
        clearPlayer(event.getEliminatedPlayer());
    }

    private void onGameEnd(@NotNull BlitzGameEndEvent event) {
        for (var player : event.getGame().getBlitzPlayers()) {
            clearPlayer(player.getUuid());
        }
    }
}
