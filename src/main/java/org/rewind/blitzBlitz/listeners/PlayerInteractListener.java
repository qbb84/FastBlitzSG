package org.rewind.blitzBlitz.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ChatUtil;

public final class PlayerInteractListener implements Listener {

    private static final int BLITZ_STAR_MODEL_DATA = 9001;

    private final BlitzPlugin plugin;

    public PlayerInteractListener(@NotNull BlitzPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        Game game = plugin.getGameByPlayer(player.getUniqueId());
        if (game == null) return;

        SGPlayer sgPlayer = game.getPlayer(player.getUniqueId());
        if (sgPlayer == null || !sgPlayer.isAlive()) return;

        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (offHand.getType() != Material.NETHER_STAR) return;

        ItemMeta meta = offHand.getItemMeta();
        if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() != BLITZ_STAR_MODEL_DATA) {
            if (sgPlayer.getKit() != null && sgPlayer.getKit().hasActiveAbility()) {
                boolean activated = plugin.getKitManager().tryActivateAbility(sgPlayer);
                if (activated) {
                    ChatUtil.sendMessage(player, "&6&lAbility activated!");
                }
            }
            return;
        }

        if (sgPlayer.getKit() != null && sgPlayer.getKit().hasActiveAbility()) {
            sgPlayer.getSessionStats().addBlitzStarUsed();
            plugin.getKitManager().forceActivateAbility(sgPlayer);
            ChatUtil.sendMessage(player, "&6&l★ BLITZ STAR ACTIVATED! ★");

            offHand.setAmount(offHand.getAmount() - 1);
            player.getInventory().setItemInOffHand(offHand.getAmount() <= 0 ? null : offHand);
        }
    }
}
