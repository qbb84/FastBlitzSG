package org.rewind.blitzBlitz.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.kit.Kit;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ItemBuilder;

import java.time.Duration;
import java.util.List;

public final class RunnerKit extends Kit {

    private static final int PASSIVE_SPEED_AMPLIFIER = 0;
    private static final int ACTIVE_SPEED_AMPLIFIER = 2;
    private static final int ACTIVE_DURATION_TICKS = 100;
    private static final long COOLDOWN_SECONDS = 45;
    private static final int PERMANENT_DURATION = Integer.MAX_VALUE;

    @Override
    @NotNull
    public String getId() { return "runner"; }

    @Override
    @NotNull
    public String getDisplayName() { return "Runner"; }

    @Override
    @NotNull
    public ItemStack getIcon() {
        return new ItemBuilder(Material.SUGAR).name("&6Runner").build();
    }

    @Override
    @NotNull
    public List<ItemStack> getStartingItems() {
        return List.of();
    }

    @Override
    @NotNull
    public String getPassiveDescription() { return "Permanent Speed I"; }

    @Override
    @NotNull
    public String getActiveDescription() { return "Speed III for 5 seconds (45s cooldown)"; }

    @Override
    public void onGameStart(@NotNull SGPlayer sgPlayer) {
        Player player = sgPlayer.getBukkitPlayer();
        if (player != null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PERMANENT_DURATION, PASSIVE_SPEED_AMPLIFIER, true, false));
        }
    }

    @Override
    public void onPlayerKill(@NotNull SGPlayer killer, @NotNull SGPlayer victim) {}

    @Override
    public void onPlayerDamaged(@NotNull SGPlayer player, @NotNull SGPlayer attacker, double damage) {}

    @Override
    public boolean hasActiveAbility() { return true; }

    @Override
    public void activateAbility(@NotNull SGPlayer sgPlayer) {
        Player player = sgPlayer.getBukkitPlayer();
        if (player == null) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ACTIVE_DURATION_TICKS, ACTIVE_SPEED_AMPLIFIER, true, true));
    }

    @Override
    @NotNull
    public Duration getAbilityCooldown() { return Duration.ofSeconds(COOLDOWN_SECONDS); }
}
