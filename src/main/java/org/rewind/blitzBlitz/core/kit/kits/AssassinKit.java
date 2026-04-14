package org.rewind.blitzBlitz.core.kit.kits;

import org.bukkit.Bukkit;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AssassinKit extends Kit {

    private static final int INVISIBILITY_DURATION_TICKS = 120;
    private static final long COOLDOWN_SECONDS = 90;

    private final Map<UUID, ItemStack[]> storedArmour = new HashMap<>();

    @Override
    @NotNull
    public String getId() { return "assassin"; }

    @Override
    @NotNull
    public String getDisplayName() { return "Assassin"; }

    @Override
    @NotNull
    public ItemStack getIcon() {
        return new ItemBuilder(Material.FERMENTED_SPIDER_EYE).name("&6Assassin").build();
    }

    @Override
    @NotNull
    public List<ItemStack> getStartingItems() {
        return List.of();
    }

    @Override
    @NotNull
    public String getPassiveDescription() { return "None"; }

    @Override
    @NotNull
    public String getActiveDescription() { return "Full invisibility + hide armour for 6s. Armour re-equips on hit. (90s cooldown)"; }

    @Override
    public void onGameStart(@NotNull SGPlayer player) {}

    @Override
    public void onPlayerKill(@NotNull SGPlayer killer, @NotNull SGPlayer victim) {}

    @Override
    public void onPlayerDamaged(@NotNull SGPlayer sgPlayer, @NotNull SGPlayer attacker, double damage) {
        restoreArmour(sgPlayer);
    }

    @Override
    public boolean hasActiveAbility() { return true; }

    @Override
    public void activateAbility(@NotNull SGPlayer sgPlayer) {
        Player player = sgPlayer.getBukkitPlayer();
        if (player == null) return;

        storedArmour.put(sgPlayer.getUuid(), player.getInventory().getArmorContents().clone());
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, INVISIBILITY_DURATION_TICKS, 0, false, false));

        Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("BlitzBlitz"),
                () -> restoreArmour(sgPlayer),
                INVISIBILITY_DURATION_TICKS
        );
    }

    @Override
    @NotNull
    public Duration getAbilityCooldown() { return Duration.ofSeconds(COOLDOWN_SECONDS); }

    public void restoreArmour(@NotNull SGPlayer sgPlayer) {
        ItemStack[] armour = storedArmour.remove(sgPlayer.getUuid());
        if (armour == null) return;
        Player player = sgPlayer.getBukkitPlayer();
        if (player == null) return;
        player.getInventory().setArmorContents(armour);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
}
