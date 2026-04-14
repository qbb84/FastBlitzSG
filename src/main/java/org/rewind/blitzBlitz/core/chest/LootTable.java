package org.rewind.blitzBlitz.core.chest;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class LootTable {

    private final List<LootTableEntry> entries;
    private final int totalWeight;

    public LootTable(@NotNull List<LootTableEntry> entries) {
        this.entries = List.copyOf(entries);
        this.totalWeight = entries.stream().mapToInt(LootTableEntry::getWeight).sum();
    }

    @NotNull
    public List<ItemStack> generateLoot(int itemCount) {
        List<ItemStack> items = new ArrayList<>();
        if (entries.isEmpty() || totalWeight <= 0) {
            return items;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < itemCount; i++) {
            LootTableEntry entry = selectWeightedEntry(random);
            if (entry != null) {
                int amount = entry.getMinAmount() == entry.getMaxAmount()
                        ? entry.getMinAmount()
                        : random.nextInt(entry.getMinAmount(), entry.getMaxAmount() + 1);
                ItemStack item = new ItemStack(entry.getMaterial(), amount);
                if (!entry.getEnchantments().isEmpty()) {
                    ItemMeta meta = item.getItemMeta();
                    entry.getEnchantments().forEach((ench, level) -> meta.addEnchant(ench, level, true));
                    item.setItemMeta(meta);
                }
                items.add(item);
            }
        }
        return items;
    }

    private LootTableEntry selectWeightedEntry(@NotNull ThreadLocalRandom random) {
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (LootTableEntry entry : entries) {
            cumulative += entry.getWeight();
            if (roll < cumulative) {
                return entry;
            }
        }
        return entries.get(entries.size() - 1);
    }

    @NotNull
    public List<LootTableEntry> getEntries() { return entries; }
}
