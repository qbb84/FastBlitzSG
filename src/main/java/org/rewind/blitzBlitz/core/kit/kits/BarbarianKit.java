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

public final class BarbarianKit extends Kit {

    private static final int STRENGTH_DURATION_TICKS = 60;
    private static final int STRENGTH_AMPLIFIER = 0;

    @Override
    @NotNull
    public String getId() { return "barbarian"; }

    @Override
    @NotNull
    public String getDisplayName() { return "Barbarian"; }

    @Override
    @NotNull
    public ItemStack getIcon() {
        return new ItemBuilder(Material.IRON_AXE).name("&6Barbarian").build();
    }

    @Override
    @NotNull
    public List<ItemStack> getStartingItems() {
        return List.of(new ItemStack(Material.IRON_AXE));
    }

    @Override
    @NotNull
    public String getPassiveDescription() { return "Kills grant 3 seconds of Strength I"; }

    @Override
    @NotNull
    public String getActiveDescription() { return "None"; }

    @Override
    public void onGameStart(@NotNull SGPlayer player) {}

    @Override
    public void onPlayerKill(@NotNull SGPlayer killer, @NotNull SGPlayer victim) {
        Player player = killer.getBukkitPlayer();
        if (player != null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, STRENGTH_DURATION_TICKS, STRENGTH_AMPLIFIER));
        }
    }

    @Override
    public void onPlayerDamaged(@NotNull SGPlayer player, @NotNull SGPlayer attacker, double damage) {}

    @Override
    public boolean hasActiveAbility() { return false; }

    @Override
    public void activateAbility(@NotNull SGPlayer player) {}

    @Override
    @NotNull
    public Duration getAbilityCooldown() { return Duration.ZERO; }
}
