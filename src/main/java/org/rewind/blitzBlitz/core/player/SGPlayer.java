package org.rewind.blitzBlitz.core.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.api.BlitzPlayer;
import org.rewind.blitzBlitz.core.kit.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SGPlayer implements BlitzPlayer {

    private final UUID uuid;
    private final GameStats sessionStats;
    private Kit kit;
    private SGPlayerState state;
    private UUID lastAttacker;
    private long lastDamagedAt;
    private final Map<String, Long> abilityCooldowns;

    public SGPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.sessionStats = new GameStats();
        this.state = SGPlayerState.NOT_IN_GAME;
        this.abilityCooldowns = new HashMap<>();
    }

    @NotNull
    public UUID getUuid() { return uuid; }

    @Nullable
    public Player getBukkitPlayer() { return Bukkit.getPlayer(uuid); }

    @NotNull
    public GameStats getSessionStats() { return sessionStats; }

    @Nullable
    public Kit getKit() { return kit; }

    public void setKit(@Nullable Kit kit) { this.kit = kit; }

    @NotNull
    public SGPlayerState getState() { return state; }

    public void setState(@NotNull SGPlayerState state) { this.state = state; }

    public boolean isAlive() { return state == SGPlayerState.ALIVE; }

    public boolean isSpectating() { return state == SGPlayerState.SPECTATING; }

    @Nullable
    public UUID getLastAttacker() { return lastAttacker; }

    public void setLastAttacker(@Nullable UUID lastAttacker) {
        this.lastAttacker = lastAttacker;
        this.lastDamagedAt = System.currentTimeMillis();
    }

    public long getLastDamagedAt() { return lastDamagedAt; }

    @NotNull
    public Map<String, Long> getAbilityCooldowns() { return abilityCooldowns; }

    public boolean isAbilityOnCooldown(@NotNull String abilityId) {
        Long expiry = abilityCooldowns.get(abilityId);
        if (expiry == null) return false;
        return System.currentTimeMillis() < expiry;
    }

    public void setAbilityCooldown(@NotNull String abilityId, long durationMillis) {
        abilityCooldowns.put(abilityId, System.currentTimeMillis() + durationMillis);
    }

    public long getRemainingCooldown(@NotNull String abilityId) {
        Long expiry = abilityCooldowns.get(abilityId);
        if (expiry == null) return 0L;
        return Math.max(0L, expiry - System.currentTimeMillis());
    }

    @Override
    @NotNull
    public String getDisplayName() {
        Player player = getBukkitPlayer();
        return player != null ? player.getName() : uuid.toString();
    }

    @Override
    public int getKills() { return sessionStats.getKills(); }

    @Override
    public int getDeaths() { return sessionStats.getDeaths(); }

    @Override
    @Nullable
    public String getKitId() { return kit != null ? kit.getId() : null; }
}
