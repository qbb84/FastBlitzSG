package org.rewind.blitzBlitz.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.util.ChatUtil;
import org.rewind.blitzBlitz.util.ItemBuilder;

import java.util.*;

public final class CosmeticsGUI implements InventoryHolder {

    private static final int GUI_SIZE = 27;
    private static final int GRID_START_SLOT = 10;
    private static final int[] GRID_SLOTS = {10, 11, 12, 13, 14, 19, 20, 21, 22};

    private final CosmeticsManager cosmeticsManager;
    private final Map<Integer, TrailEffect> slotEffectMap;
    private final Map<UUID, TrailEffect> pendingConfirmation;
    private Inventory inventory;

    public CosmeticsGUI(@NotNull CosmeticsManager cosmeticsManager) {
        this.cosmeticsManager = cosmeticsManager;
        this.slotEffectMap = new HashMap<>();
        this.pendingConfirmation = new HashMap<>();
    }

    public void open(@NotNull Player player, int coinBalance) {
        pendingConfirmation.remove(player.getUniqueId());
        String title = ChatUtil.colorize("&6&lTrails &8| &eCoins: &6" + coinBalance);
        inventory = Bukkit.createInventory(this, GUI_SIZE, title);
        populateInventory(player.getUniqueId());
        player.openInventory(inventory);
    }

    private void populateInventory(@NotNull UUID playerId) {
        inventory.clear();
        slotEffectMap.clear();

        TrailEffect[] effects = TrailEffect.values();
        TrailEffect activeTrail = cosmeticsManager.getActiveTrail(playerId);
        Set<TrailEffect> owned = cosmeticsManager.getOwnedTrails(playerId);

        for (int i = 0; i < effects.length && i < GRID_SLOTS.length; i++) {
            TrailEffect effect = effects[i];
            int slot = GRID_SLOTS[i];
            boolean isOwned = owned.contains(effect);
            boolean isActive = effect.equals(activeTrail);

            ItemStack icon;
            if (isOwned) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add("&7Status: " + (isActive ? "&b&lACTIVE" : "&aOwned"));
                lore.add("");
                if (isActive) {
                    lore.add("&cClick to deactivate");
                } else {
                    lore.add("&eClick to activate!");
                }

                ItemBuilder builder = new ItemBuilder(effect.getIcon())
                        .name((isActive ? "&b&l" : "&a&l") + effect.getDisplayName())
                        .lore(lore)
                        .enchant(Enchantment.UNBREAKING, 1)
                        .flags(ItemFlag.HIDE_ENCHANTS);

                icon = builder.build();
            } else {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add("&7Status: &8Locked");
                lore.add("&7Cost: &6" + effect.getCoinCost() + " coins");
                lore.add("");
                lore.add("&eClick to purchase!");

                icon = new ItemBuilder(Material.GRAY_DYE)
                        .name("&7&l" + effect.getDisplayName())
                        .lore(lore)
                        .build();
            }

            inventory.setItem(slot, icon);
            slotEffectMap.put(slot, effect);
        }

        ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int slot = 0; slot < GUI_SIZE; slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        }
    }

    public boolean handleClick(@NotNull Player player, int slot) {
        TrailEffect effect = slotEffectMap.get(slot);
        if (effect == null) return false;

        UUID uuid = player.getUniqueId();
        boolean isOwned = cosmeticsManager.ownsTrail(uuid, effect);

        if (isOwned) {
            TrailEffect active = cosmeticsManager.getActiveTrail(uuid);
            if (effect.equals(active)) {
                cosmeticsManager.setActiveTrail(uuid, null);
                cosmeticsManager.savePlayerCosmetics(uuid);
                ChatUtil.sendMessage(player, "&cTrail deactivated.");
            } else {
                cosmeticsManager.setActiveTrail(uuid, effect);
                cosmeticsManager.savePlayerCosmetics(uuid);
                ChatUtil.sendMessage(player, "&aTrail activated: &b" + effect.getDisplayName());
            }
            player.closeInventory();
            return true;
        }

        TrailEffect pending = pendingConfirmation.get(uuid);
        if (pending != null && pending == effect) {
            cosmeticsManager.purchaseTrail(uuid, effect);
            cosmeticsManager.setActiveTrail(uuid, effect);
            cosmeticsManager.savePlayerCosmetics(uuid);
            ChatUtil.sendMessage(player, "&aPurchased and activated: &b" + effect.getDisplayName());
            player.closeInventory();
            pendingConfirmation.remove(uuid);
            return true;
        }

        pendingConfirmation.put(uuid, effect);
        ChatUtil.sendMessage(player, "&ePurchase &6" + effect.getDisplayName() + " &efor &6" +
                effect.getCoinCost() + " coins&e? Click again to confirm!");
        return true;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }
}
