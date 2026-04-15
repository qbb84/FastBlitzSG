package org.rewind.blitzBlitz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.cosmetics.CosmeticsGUI;
import org.rewind.blitzBlitz.gui.KitSelectionGUI;

public final class InventoryClickListener implements Listener {

    private final BlitzPlugin plugin;

    public InventoryClickListener(@NotNull BlitzPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof KitSelectionGUI kitGui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            kitGui.handleClick(player, event.getRawSlot());
        } else if (holder instanceof CosmeticsGUI cosmeticsGui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            cosmeticsGui.handleClick(player, event.getRawSlot());
        }
    }
}
