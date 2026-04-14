package org.rewind.blitzBlitz.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.kit.Kit;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ItemBuilder;

import java.time.Duration;
import java.util.List;

public final class TamerKit extends Kit {

    private static final int WOLF_COUNT = 3;

    @Override
    @NotNull
    public String getId() { return "tamer"; }

    @Override
    @NotNull
    public String getDisplayName() { return "Tamer"; }

    @Override
    @NotNull
    public ItemStack getIcon() {
        return new ItemBuilder(Material.BONE).name("&6Tamer").build();
    }

    @Override
    @NotNull
    public List<ItemStack> getStartingItems() {
        return List.of(new ItemStack(Material.BONE, WOLF_COUNT));
    }

    @Override
    @NotNull
    public String getPassiveDescription() { return "Start with 3 tamed wolves that attack your enemies"; }

    @Override
    @NotNull
    public String getActiveDescription() { return "None"; }

    @Override
    public void onGameStart(@NotNull SGPlayer sgPlayer) {
        Player player = sgPlayer.getBukkitPlayer();
        if (player == null) return;
        for (int i = 0; i < WOLF_COUNT; i++) {
            Wolf wolf = player.getWorld().spawn(player.getLocation(), Wolf.class);
            wolf.setOwner(player);
            wolf.setTamed(true);
        }
    }

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
}
