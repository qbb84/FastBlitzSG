package org.rewind.blitzBlitz.core.kit.kits;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.kit.Kit;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ItemBuilder;

import java.time.Duration;
import java.util.*;

public final class NecromancerKit extends Kit {

    private static final int MAX_SKELETONS = 2;
    private static final long SKELETON_LIFETIME_TICKS = 600L;

    private final Map<UUID, List<Skeleton>> activeSkeletons = new HashMap<>();

    @Override
    @NotNull
    public String getId() { return "necromancer"; }

    @Override
    @NotNull
    public String getDisplayName() { return "Necromancer"; }

    @Override
    @NotNull
    public ItemStack getIcon() {
        return new ItemBuilder(Material.SKELETON_SKULL).name("&6Necromancer").build();
    }

    @Override
    @NotNull
    public List<ItemStack> getStartingItems() {
        return List.of();
    }

    @Override
    @NotNull
    public String getPassiveDescription() { return "Killed players leave a skeleton that fights for you for 30s (max 2)"; }

    @Override
    @NotNull
    public String getActiveDescription() { return "None"; }

    @Override
    public void onGameStart(@NotNull SGPlayer player) {}

    @Override
    public void onPlayerKill(@NotNull SGPlayer killer, @NotNull SGPlayer victim) {
        Player killerPlayer = killer.getBukkitPlayer();
        Player victimPlayer = victim.getBukkitPlayer();
        if (killerPlayer == null || victimPlayer == null) return;

        List<Skeleton> skeletons = activeSkeletons.computeIfAbsent(killer.getUuid(), k -> new ArrayList<>());
        skeletons.removeIf(s -> s.isDead() || !s.isValid());

        if (skeletons.size() >= MAX_SKELETONS) {
            Skeleton oldest = skeletons.remove(0);
            oldest.remove();
        }

        Skeleton skeleton = victimPlayer.getWorld().spawn(victimPlayer.getLocation(), Skeleton.class);
        skeleton.setCustomName(killerPlayer.getName() + "'s Minion");
        skeleton.setCustomNameVisible(true);
        skeleton.setTarget(null);
        skeletons.add(skeleton);

        Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("BlitzBlitz"),
                () -> {
                    if (skeleton.isValid() && !skeleton.isDead()) {
                        skeleton.remove();
                    }
                    skeletons.remove(skeleton);
                },
                SKELETON_LIFETIME_TICKS
        );
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

    public void cleanup(@NotNull UUID playerId) {
        List<Skeleton> skeletons = activeSkeletons.remove(playerId);
        if (skeletons != null) {
            skeletons.forEach(s -> { if (s.isValid()) s.remove(); });
        }
    }

    @NotNull
    public Map<UUID, List<Skeleton>> getActiveSkeletons() {
        return activeSkeletons;
    }
}
