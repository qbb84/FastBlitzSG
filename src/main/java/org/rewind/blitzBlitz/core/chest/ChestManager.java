package org.rewind.blitzBlitz.core.chest;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.config.BlitzConfig;
import org.rewind.blitzBlitz.core.arena.Arena;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public final class ChestManager {

    private static final int MIN_ITEMS_COMMON = 3;
    private static final int MAX_ITEMS_COMMON = 5;
    private static final int MIN_ITEMS_UNCOMMON = 4;
    private static final int MAX_ITEMS_UNCOMMON = 6;
    private static final int MIN_ITEMS_RARE = 5;
    private static final int MAX_ITEMS_RARE = 7;
    private static final int MIN_ITEMS_CENTRE = 6;
    private static final int MAX_ITEMS_CENTRE = 8;

    private final Map<ChestTier, LootTable> lootTables;
    private final Map<String, Set<Location>> filledChests;
    private final Map<String, Map<UUID, Map<Location, Long>>> playerChestCooldowns;
    private final int chestCooldownSeconds;

    public ChestManager(@NotNull BlitzConfig config) {
        this.lootTables = new EnumMap<>(ChestTier.class);
        this.filledChests = new ConcurrentHashMap<>();
        this.playerChestCooldowns = new ConcurrentHashMap<>();
        this.chestCooldownSeconds = config.getChestCooldownSeconds();
    }

    public void registerLootTable(@NotNull ChestTier tier, @NotNull LootTable table) {
        lootTables.put(tier, table);
    }

    public void fillChests(@NotNull Arena arena) {
        String arenaName = arena.getName();
        filledChests.put(arenaName, new HashSet<>());

        arena.getChestLocations().forEach((location, tier) -> {
            Block block = location.getBlock();
            if (block.getState() instanceof Chest chest) {
                fillChest(chest.getInventory(), tier);
                filledChests.get(arenaName).add(location);
            }
        });
    }

    public void refillChests(@NotNull Arena arena) {
        String arenaName = arena.getName();
        playerChestCooldowns.remove(arenaName);
        fillChests(arena);
    }

    private void fillChest(@NotNull Inventory inventory, @NotNull ChestTier tier) {
        inventory.clear();
        LootTable table = lootTables.get(tier);
        if (table == null) return;

        int itemCount = getItemCount(tier);
        List<ItemStack> items = table.generateLoot(itemCount);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int slots = inventory.getSize();
        Set<Integer> usedSlots = new HashSet<>();
        for (ItemStack item : items) {
            int slot;
            int attempts = 0;
            do {
                slot = random.nextInt(slots);
                attempts++;
            } while (usedSlots.contains(slot) && attempts < slots);
            usedSlots.add(slot);
            inventory.setItem(slot, item);
        }
    }

    private int getItemCount(@NotNull ChestTier tier) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return switch (tier) {
            case COMMON -> random.nextInt(MIN_ITEMS_COMMON, MAX_ITEMS_COMMON + 1);
            case UNCOMMON -> random.nextInt(MIN_ITEMS_UNCOMMON, MAX_ITEMS_UNCOMMON + 1);
            case RARE -> random.nextInt(MIN_ITEMS_RARE, MAX_ITEMS_RARE + 1);
            case CENTRE -> random.nextInt(MIN_ITEMS_CENTRE, MAX_ITEMS_CENTRE + 1);
        };
    }

    public boolean isOnCooldown(@NotNull String arenaName, @NotNull UUID playerId, @NotNull Location chestLocation) {
        Map<UUID, Map<Location, Long>> arenaMap = playerChestCooldowns.get(arenaName);
        if (arenaMap == null) return false;
        Map<Location, Long> playerMap = arenaMap.get(playerId);
        if (playerMap == null) return false;
        Long lastLooted = playerMap.get(chestLocation);
        if (lastLooted == null) return false;
        return (System.currentTimeMillis() - lastLooted) < (chestCooldownSeconds * 1000L);
    }

    public void markLooted(@NotNull String arenaName, @NotNull UUID playerId, @NotNull Location chestLocation) {
        playerChestCooldowns
                .computeIfAbsent(arenaName, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                .put(chestLocation, System.currentTimeMillis());
    }

    public void clearArenaData(@NotNull String arenaName) {
        filledChests.remove(arenaName);
        playerChestCooldowns.remove(arenaName);
    }

    @NotNull
    public Map<ChestTier, LootTable> getLootTables() {
        return lootTables;
    }
}
