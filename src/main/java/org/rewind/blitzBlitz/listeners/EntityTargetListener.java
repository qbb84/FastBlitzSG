package org.rewind.blitzBlitz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.player.SGPlayer;

public final class EntityTargetListener implements Listener {

    private final BlitzPlugin plugin;

    public EntityTargetListener(@NotNull BlitzPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player target)) return;

        Game game = plugin.getGameByPlayer(target.getUniqueId());
        if (game == null) return;

        SGPlayer sgPlayer = game.getPlayer(target.getUniqueId());
        if (sgPlayer != null && sgPlayer.isSpectating()) {
            event.setCancelled(true);
        }
    }
}
