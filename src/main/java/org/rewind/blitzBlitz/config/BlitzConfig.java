package org.rewind.blitzBlitz.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class BlitzConfig {

    private final int minPlayers;
    private final int maxPlayers;
    private final int countdownSeconds;
    private final int gracePeriodSeconds;
    private final int deathmatchBorderTime;
    private final int deathmatchFinalSize;
    private final double deathmatchBorderDamage;
    private final int endedDelaySeconds;
    private final int chestCooldownSeconds;
    private final int combatTagSeconds;
    private final int scoreboardUpdateTicks;
    private final String lobbyWorld;

    public BlitzConfig(@NotNull JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration cfg = plugin.getConfig();
        Logger log = plugin.getLogger();

        this.minPlayers = validatePositive(cfg.getInt("game.min-players", 4), "game.min-players", 4, log);
        this.maxPlayers = validatePositive(cfg.getInt("game.max-players", 24), "game.max-players", 24, log);
        this.countdownSeconds = validatePositive(cfg.getInt("game.countdown-seconds", 30), "game.countdown-seconds", 30, log);
        this.gracePeriodSeconds = validatePositive(cfg.getInt("game.grace-period-seconds", 60), "game.grace-period-seconds", 60, log);
        this.deathmatchBorderTime = validatePositive(cfg.getInt("game.deathmatch-border-time", 180), "game.deathmatch-border-time", 180, log);
        this.deathmatchFinalSize = validatePositive(cfg.getInt("game.deathmatch-final-size", 10), "game.deathmatch-final-size", 10, log);
        this.deathmatchBorderDamage = cfg.getDouble("game.deathmatch-border-damage", 4.0);
        this.endedDelaySeconds = validatePositive(cfg.getInt("game.ended-delay-seconds", 10), "game.ended-delay-seconds", 10, log);
        this.chestCooldownSeconds = validatePositive(cfg.getInt("game.chest-cooldown-seconds", 30), "game.chest-cooldown-seconds", 30, log);
        this.combatTagSeconds = validatePositive(cfg.getInt("game.combat-tag-seconds", 15), "game.combat-tag-seconds", 15, log);
        this.scoreboardUpdateTicks = validatePositive(cfg.getInt("game.scoreboard-update-ticks", 2), "game.scoreboard-update-ticks", 2, log);
        this.lobbyWorld = cfg.getString("lobby.world", "world");

        if (minPlayers > maxPlayers) {
            log.warning("[Config] game.min-players (" + minPlayers + ") is greater than game.max-players (" + maxPlayers + "). This may cause issues.");
        }

        log.info("[Config] Configuration loaded and validated successfully.");
    }

    private int validatePositive(int value, @NotNull String path, int defaultValue, @NotNull Logger log) {
        if (value <= 0) {
            log.warning("[Config] '" + path + "' must be positive, was " + value + ". Using default: " + defaultValue);
            return defaultValue;
        }
        return value;
    }

    public int getMinPlayers() { return minPlayers; }
    public int getMaxPlayers() { return maxPlayers; }
    public int getCountdownSeconds() { return countdownSeconds; }
    public int getGracePeriodSeconds() { return gracePeriodSeconds; }
    public int getDeathmatchBorderTime() { return deathmatchBorderTime; }
    public int getDeathmatchFinalSize() { return deathmatchFinalSize; }
    public double getDeathmatchBorderDamage() { return deathmatchBorderDamage; }
    public int getEndedDelaySeconds() { return endedDelaySeconds; }
    public int getChestCooldownSeconds() { return chestCooldownSeconds; }
    public int getCombatTagSeconds() { return combatTagSeconds; }
    public int getScoreboardUpdateTicks() { return scoreboardUpdateTicks; }

    @NotNull
    public String getLobbyWorld() { return lobbyWorld; }
}
