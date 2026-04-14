package org.rewind.blitzBlitz.config;

import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.chest.ChestManager;
import org.rewind.blitzBlitz.core.chest.ChestTier;
import org.rewind.blitzBlitz.core.chest.LootTableBuilder;
import org.rewind.blitzBlitz.core.chest.LootTableEntry;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class LootConfig {

    private LootConfig() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void load(@NotNull JavaPlugin plugin, @NotNull ChestManager chestManager) {
        Logger log = plugin.getLogger();

        File file = new File(plugin.getDataFolder(), "loot.yml");
        if (!file.exists()) {
            plugin.saveResource("loot.yml", false);
        }

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection tablesSection = cfg.getConfigurationSection("loot-tables");
        if (tablesSection == null) {
            log.warning("[Loot] No 'loot-tables' section found in loot.yml");
            return;
        }

        for (String tierKey : tablesSection.getKeys(false)) {
            ChestTier tier;
            try {
                tier = ChestTier.valueOf(tierKey.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warning("[Loot] Unknown chest tier: " + tierKey);
                continue;
            }

            ConfigurationSection tierSection = tablesSection.getConfigurationSection(tierKey);
            if (tierSection == null) continue;

            LootTableBuilder builder = new LootTableBuilder();
            for (String entryKey : tierSection.getKeys(false)) {
                ConfigurationSection entrySection = tierSection.getConfigurationSection(entryKey);
                if (entrySection == null) continue;

                String materialName = entrySection.getString("item", "STONE");
                Material material = Material.matchMaterial(materialName);
                if (material == null) {
                    log.warning("[Loot] Unknown material: " + materialName + " in tier " + tierKey);
                    continue;
                }

                int weight = entrySection.getInt("weight", 10);
                int minAmount = 1;
                int maxAmount = 1;

                String amountStr = entrySection.getString("amount", "1");
                if (amountStr.contains("-")) {
                    String[] parts = amountStr.split("-");
                    minAmount = Integer.parseInt(parts[0].trim());
                    maxAmount = Integer.parseInt(parts[1].trim());
                } else {
                    minAmount = Integer.parseInt(amountStr.trim());
                    maxAmount = minAmount;
                }

                Map<Enchantment, Integer> enchantments = new HashMap<>();
                ConfigurationSection enchSection = entrySection.getConfigurationSection("enchants");
                if (enchSection != null) {
                    for (String enchKey : enchSection.getKeys(false)) {
                        Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchKey.toLowerCase()));
                        if (ench != null) {
                            enchantments.put(ench, enchSection.getInt(enchKey, 1));
                        } else {
                            log.warning("[Loot] Unknown enchantment: " + enchKey);
                        }
                    }
                }

                builder.addEntry(material, minAmount, maxAmount, weight, enchantments);
            }

            chestManager.registerLootTable(tier, builder.build());
            log.info("[Loot] Loaded loot table for tier: " + tier.name());
        }
    }
}
