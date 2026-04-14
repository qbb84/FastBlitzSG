package org.rewind.blitzBlitz.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(@NotNull Material material) {
        this(material, 1);
    }

    public ItemBuilder(@NotNull Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(@NotNull ItemStack source) {
        this.item = source.clone();
        this.meta = item.getItemMeta();
    }

    @NotNull
    public ItemBuilder name(@NotNull String name) {
        meta.setDisplayName(ChatUtil.colorize(name));
        return this;
    }

    @NotNull
    public ItemBuilder lore(@NotNull String... lines) {
        List<String> lore = new ArrayList<>();
        for (String line : lines) {
            lore.add(ChatUtil.colorize(line));
        }
        meta.setLore(lore);
        return this;
    }

    @NotNull
    public ItemBuilder lore(@NotNull List<String> lines) {
        List<String> lore = new ArrayList<>();
        for (String line : lines) {
            lore.add(ChatUtil.colorize(line));
        }
        meta.setLore(lore);
        return this;
    }

    @NotNull
    public ItemBuilder enchant(@NotNull Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    @NotNull
    public ItemBuilder flags(@NotNull ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    @NotNull
    public ItemBuilder unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    @NotNull
    public ItemBuilder customModelData(int data) {
        meta.setCustomModelData(data);
        return this;
    }

    @NotNull
    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    @NotNull
    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
