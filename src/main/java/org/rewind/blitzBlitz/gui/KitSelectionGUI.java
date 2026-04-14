package org.rewind.blitzBlitz.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.kit.Kit;
import org.rewind.blitzBlitz.core.kit.KitFactory;
import org.rewind.blitzBlitz.core.kit.KitManager;
import org.rewind.blitzBlitz.util.ChatUtil;
import org.rewind.blitzBlitz.util.ItemBuilder;

import java.util.*;

public final class KitSelectionGUI implements InventoryHolder {

    private static final int GUI_SIZE = 27;
    private static final String GUI_TITLE = "&6&lSelect Your Kit";

    private final Inventory inventory;
    private final Map<Integer, Kit> slotKitMap;
    private final KitFactory kitFactory;
    private final KitManager kitManager;
    private final Map<UUID, String> pendingConfirmation;

    public KitSelectionGUI(@NotNull KitFactory kitFactory, @NotNull KitManager kitManager) {
        this.kitFactory = kitFactory;
        this.kitManager = kitManager;
        this.slotKitMap = new HashMap<>();
        this.pendingConfirmation = new HashMap<>();
        this.inventory = Bukkit.createInventory(this, GUI_SIZE, ChatUtil.colorize(GUI_TITLE));
        populateInventory();
    }

    private void populateInventory() {
        inventory.clear();
        slotKitMap.clear();
        int slot = 0;
        for (Kit kit : kitFactory.getAll()) {
            if (slot >= GUI_SIZE) break;

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&7Passive: &f" + kit.getPassiveDescription());
            lore.add("&7Active: &f" + kit.getActiveDescription());
            if (kit.hasActiveAbility()) {
                lore.add("&7Cooldown: &e" + kit.getAbilityCooldown().getSeconds() + "s");
            }
            lore.add("");
            lore.add("&eClick to select!");

            ItemStack icon = new ItemBuilder(kit.getIcon())
                    .name("&6&l" + kit.getDisplayName())
                    .lore(lore)
                    .build();

            inventory.setItem(slot, icon);
            slotKitMap.put(slot, kit);
            slot++;
        }
    }

    public void open(@NotNull Player player) {
        pendingConfirmation.remove(player.getUniqueId());
        populateInventory();
        player.openInventory(inventory);
    }

    public boolean handleClick(@NotNull Player player, int slot) {
        Kit kit = slotKitMap.get(slot);
        if (kit == null) return false;

        UUID uuid = player.getUniqueId();
        String pendingKit = pendingConfirmation.get(uuid);

        if (pendingKit != null && pendingKit.equals(kit.getId())) {
            kitManager.selectKit(uuid, kit.getId());
            ChatUtil.sendMessage(player, "&aYou selected &e" + kit.getDisplayName() + "&a!");
            player.closeInventory();
            pendingConfirmation.remove(uuid);
            return true;
        }

        pendingConfirmation.put(uuid, kit.getId());
        ChatUtil.sendMessage(player, "&eClick &6" + kit.getDisplayName() + " &eagain to confirm!");
        return true;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }
}
