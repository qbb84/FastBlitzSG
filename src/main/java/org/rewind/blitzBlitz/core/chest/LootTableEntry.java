package org.rewind.blitzBlitz.core.chest;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class LootTableEntry {

    private final Material material;
    private final int minAmount;
    private final int maxAmount;
    private final int weight;
    private final Map<Enchantment, Integer> enchantments;

    public LootTableEntry(@NotNull Material material, int minAmount, int maxAmount, int weight,
                           @NotNull Map<Enchantment, Integer> enchantments) {
        this.material = material;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.weight = weight;
        this.enchantments = enchantments;
    }

    @NotNull
    public Material getMaterial() { return material; }
    public int getMinAmount() { return minAmount; }
    public int getMaxAmount() { return maxAmount; }
    public int getWeight() { return weight; }

    @NotNull
    public Map<Enchantment, Integer> getEnchantments() { return enchantments; }
}
