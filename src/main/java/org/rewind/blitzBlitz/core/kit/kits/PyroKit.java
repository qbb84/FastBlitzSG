package org.rewind.blitzBlitz.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.kit.Kit;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ItemBuilder;

import java.time.Duration;
import java.util.List;

public final class PyroKit extends Kit {

    private static final int FIRE_CHARGE_COUNT = 3;
    private static final int FIRE_TICKS = 60;

    @Override
    @NotNull
    public String getId() { return "pyro"; }

    @Override
    @NotNull
    public String getDisplayName() { return "Pyro"; }

    @Override
    @NotNull
    public ItemStack getIcon() {
        return new ItemBuilder(Material.FLINT_AND_STEEL).name("&6Pyro").build();
    }

    @Override
    @NotNull
    public List<ItemStack> getStartingItems() {
        return List.of(
                new ItemStack(Material.FLINT_AND_STEEL),
                new ItemStack(Material.FIRE_CHARGE, FIRE_CHARGE_COUNT)
        );
    }

    @Override
    @NotNull
    public String getPassiveDescription() { return "Arrows set targets on fire, fire damage credits kills to you"; }

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

    public static void applyFire(@NotNull Player target) {
        target.setFireTicks(FIRE_TICKS);
    }
}
