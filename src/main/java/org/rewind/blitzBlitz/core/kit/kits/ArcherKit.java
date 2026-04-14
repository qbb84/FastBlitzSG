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

public final class ArcherKit extends Kit {

    private static final int ARROW_COUNT = 32;
    private static final int SLOWNESS_DURATION_TICKS = 40;
    private static final int SLOWNESS_AMPLIFIER = 0;

    @Override
    @NotNull
    public String getId() { return "archer"; }

    @Override
    @NotNull
    public String getDisplayName() { return "Archer"; }

    @Override
    @NotNull
    public ItemStack getIcon() {
        return new ItemBuilder(Material.BOW).name("&6Archer").build();
    }

    @Override
    @NotNull
    public List<ItemStack> getStartingItems() {
        return List.of(
                new ItemStack(Material.BOW),
                new ItemStack(Material.ARROW, ARROW_COUNT)
        );
    }

    @Override
    @NotNull
    public String getPassiveDescription() { return "Arrows apply Slowness I for 2 seconds on hit"; }

    @Override
    @NotNull
    public String getActiveDescription() { return "None"; }

    @Override
    public void onGameStart(@NotNull SGPlayer player) {}

    @Override
    public void onPlayerKill(@NotNull SGPlayer killer, @NotNull SGPlayer victim) {}

    @Override
    public void onPlayerDamaged(@NotNull SGPlayer player, @NotNull SGPlayer attacker, double damage) {}

    @Override
    public boolean hasActiveAbility() { return false; }

    @Override
    public void activateAbility(@NotNull SGPlayer player) {}

    @Override
    @NotNull
    public Duration getAbilityCooldown() { return Duration.ZERO; }

    public static void applySlowness(@NotNull Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, SLOWNESS_DURATION_TICKS, SLOWNESS_AMPLIFIER));
    }
}
