package org.rewind.blitzBlitz.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.kit.Kit;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ItemBuilder;

import java.time.Duration;
import java.util.List;

public final class ArmoursmithKit extends Kit {

    private static final int REPAIR_AMOUNT = 1;
    private static final long REPAIR_INTERVAL_TICKS = 200L;

    @Override
    @NotNull
    public String getId() { return "armoursmith"; }

    @Override
    @NotNull
    public String getDisplayName() { return "Armoursmith"; }

    @Override
    @NotNull
    public ItemStack getIcon() {
        return new ItemBuilder(Material.IRON_CHESTPLATE).name("&6Armoursmith").build();
    }

    @Override
    @NotNull
    public List<ItemStack> getStartingItems() {
        return List.of(new ItemStack(Material.IRON_CHESTPLATE));
    }

    @Override
    @NotNull
    public String getPassiveDescription() { return "Damaged armour self-repairs 1 durability every 10 seconds"; }

    @Override
    @NotNull
    public String getActiveDescription() { return "None"; }

    @Override
    public void onGameStart(@NotNull SGPlayer player) {
        Player p = player.getBukkitPlayer();
        if (p != null) {
            p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
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

    public static void tickRepair(@NotNull SGPlayer sgPlayer) {
        Player player = sgPlayer.getBukkitPlayer();
        if (player == null) return;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                ItemMeta meta = armor.getItemMeta();
                if (meta instanceof Damageable damageable && damageable.getDamage() > 0) {
                    damageable.setDamage(Math.max(0, damageable.getDamage() - REPAIR_AMOUNT));
                    armor.setItemMeta(meta);
                }
            }
        }
    }
}
