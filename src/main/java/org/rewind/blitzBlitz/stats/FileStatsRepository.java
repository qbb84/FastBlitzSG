package org.rewind.blitzBlitz.stats;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.core.player.GameStats;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class FileStatsRepository implements StatsRepository {

    private final File statsDir;
    private final Logger logger;
    private final Map<UUID, GameStats> cache;

    public FileStatsRepository(@NotNull File dataFolder, @NotNull Logger logger) {
        this.statsDir = new File(dataFolder, "stats");
        this.logger = logger;
        this.cache = new ConcurrentHashMap<>();
        if (!statsDir.exists()) {
            statsDir.mkdirs();
        }
    }

    @Override
    public void saveStats(@NotNull UUID uuid, @NotNull GameStats sessionStats) {
        GameStats lifetime = loadStats(uuid);
        if (lifetime == null) {
            lifetime = new GameStats();
        }
        lifetime.merge(sessionStats);
        cache.put(uuid, lifetime);

        File file = new File(statsDir, uuid + ".yml");
        FileConfiguration cfg = new YamlConfiguration();
        cfg.set("kills", lifetime.getKills());
        cfg.set("deaths", lifetime.getDeaths());
        cfg.set("wins", lifetime.getWins());
        cfg.set("games-played", lifetime.getGamesPlayed());
        cfg.set("damage-dealt", lifetime.getDamageDealt());
        cfg.set("damage-received", lifetime.getDamageReceived());
        cfg.set("chests-looted", lifetime.getChestsLooted());
        cfg.set("blitz-stars-used", lifetime.getBlitzStarsUsed());
        cfg.set("assists", lifetime.getAssists());

        try {
            cfg.save(file);
        } catch (IOException e) {
            logger.severe("[Stats] Failed to save stats for " + uuid + ": " + e.getMessage());
        }
    }

    @Override
    @Nullable
    public GameStats loadStats(@NotNull UUID uuid) {
        GameStats cached = cache.get(uuid);
        if (cached != null) return cached;

        File file = new File(statsDir, uuid + ".yml");
        if (!file.exists()) return null;

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        GameStats stats = new GameStats();
        stats.setKills(cfg.getInt("kills", 0));
        stats.setDeaths(cfg.getInt("deaths", 0));
        stats.setWins(cfg.getInt("wins", 0));
        stats.setGamesPlayed(cfg.getInt("games-played", 0));
        stats.setDamageDealt(cfg.getDouble("damage-dealt", 0));
        stats.setDamageReceived(cfg.getDouble("damage-received", 0));
        stats.setChestsLooted(cfg.getInt("chests-looted", 0));
        stats.setBlitzStarsUsed(cfg.getInt("blitz-stars-used", 0));
        stats.setAssists(cfg.getInt("assists", 0));

        cache.put(uuid, stats);
        return stats;
    }

    @Override
    @NotNull
    public List<Map.Entry<UUID, GameStats>> findTopPlayers(int limit, @NotNull StatType type) {
        loadAllStats();

        Comparator<Map.Entry<UUID, GameStats>> comparator = (a, b) -> {
            double valA = getStatValue(a.getValue(), type);
            double valB = getStatValue(b.getValue(), type);
            return Double.compare(valB, valA);
        };

        return cache.entrySet().stream()
                .sorted(comparator)
                .limit(limit)
                .toList();
    }

    private void loadAllStats() {
        File[] files = statsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;
        for (File file : files) {
            String uuidStr = file.getName().replace(".yml", "");
            try {
                UUID uuid = UUID.fromString(uuidStr);
                if (!cache.containsKey(uuid)) {
                    loadStats(uuid);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private double getStatValue(@NotNull GameStats stats, @NotNull StatType type) {
        return switch (type) {
            case KILLS -> stats.getKills();
            case DEATHS -> stats.getDeaths();
            case WINS -> stats.getWins();
            case GAMES_PLAYED -> stats.getGamesPlayed();
            case DAMAGE_DEALT -> stats.getDamageDealt();
            case DAMAGE_RECEIVED -> stats.getDamageReceived();
            case CHESTS_LOOTED -> stats.getChestsLooted();
            case BLITZ_STARS_USED -> stats.getBlitzStarsUsed();
            case ASSISTS -> stats.getAssists();
        };
    }
}
