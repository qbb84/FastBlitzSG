package org.rewind.blitzBlitz.core.chest;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LootTableBuilder {

    private final List<LootTableEntry> entries = new ArrayList<>();

    @NotNull
    public LootTableBuilder addEntry(@NotNull Material material, int minAmount, int maxAmount, int weight) {
        entries.add(new LootTableEntry(material, minAmount, maxAmount, weight, new HashMap<>()));
        return this;
    }

    @NotNull
    public LootTableBuilder addEntry(@NotNull Material material, int minAmount, int maxAmount, int weight,
                                      @NotNull Map<Enchantment, Integer> enchantments) {
        entries.add(new LootTableEntry(material, minAmount, maxAmount, weight, enchantments));
        return this;
    }

    @NotNull
    public LootTableBuilder addEntry(@NotNull LootTableEntry entry) {
        entries.add(entry);
        return this;
    }

    @NotNull
    public LootTable build() {
        return new LootTable(entries);
    }
}
