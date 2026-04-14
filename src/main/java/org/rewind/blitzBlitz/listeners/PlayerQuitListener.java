package org.rewind.blitzBlitz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.player.SGPlayer;

public final class PlayerQuitListener implements Listener {

    private final BlitzPlugin plugin;

    public PlayerQuitListener(@NotNull BlitzPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Game game = plugin.getGameByPlayer(player.getUniqueId());
        if (game == null) {
            plugin.getPlayerManager().remove(player.getUniqueId());
            return;
        }

        SGPlayer sgPlayer = game.getPlayer(player.getUniqueId());
        if (sgPlayer != null && sgPlayer.isAlive()) {
            sgPlayer.getSessionStats().addDeath();
            game.broadcast("&c" + player.getName() + " &edisconnected and was eliminated.");

            plugin.getSpectatorManager().makeSpectator(sgPlayer, game);
            game.removePlayer(player.getUniqueId());

            if (game.getAliveCount() <= 1 && game.getState() != GameState.ENDED) {
                game.transition(GameState.ENDED);
            }
        } else {
            game.removePlayer(player.getUniqueId());
        }

        plugin.getPlayerManager().remove(player.getUniqueId());
    }
}
