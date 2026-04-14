package org.rewind.blitzBlitz.core.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class PlayerManager {

    private final PlayerRepository repository;

    public PlayerManager(@NotNull PlayerRepository repository) {
        this.repository = repository;
    }

    @NotNull
    public SGPlayer getOrCreate(@NotNull UUID uuid) {
        SGPlayer existing = repository.findByUuid(uuid);
        if (existing != null) {
            return existing;
        }
        SGPlayer player = new SGPlayer(uuid);
        repository.save(player);
        return player;
    }

    @Nullable
    public SGPlayer get(@NotNull UUID uuid) {
        return repository.findByUuid(uuid);
    }

    public void remove(@NotNull UUID uuid) {
        repository.remove(uuid);
    }

    public void resetPlayer(@NotNull Player player) {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setExp(0f);
        player.setLevel(0);
        player.setFireTicks(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public void setSpectator(@NotNull SGPlayer sgPlayer) {
        sgPlayer.setState(SGPlayerState.SPECTATING);
        Player player = sgPlayer.getBukkitPlayer();
        if (player != null) {
            player.setGameMode(GameMode.SPECTATOR);
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    @NotNull
    public PlayerRepository getRepository() {
        return repository;
    }
}
