package org.rewind.blitzBlitz.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.api.events.BlitzPlayerEliminatedEvent;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.kit.Kit;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.Set;
import java.util.UUID;

public final class PlayerDeathListener implements Listener {

    private final BlitzPlugin plugin;

    public PlayerDeathListener(@NotNull BlitzPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Game game = plugin.getGameByPlayer(victim.getUniqueId());
        if (game == null) return;

        SGPlayer sgVictim = game.getPlayer(victim.getUniqueId());
        if (sgVictim == null || !sgVictim.isAlive()) return;

        event.setKeepInventory(false);
        event.setKeepLevel(false);
        event.deathMessage(null);

        sgVictim.getSessionStats().addDeath();

        UUID killerId = game.getCombatTracker().getLastAttacker(victim.getUniqueId());
        SGPlayer sgKiller = killerId != null ? game.getPlayer(killerId) : null;

        if (sgKiller != null) {
            sgKiller.getSessionStats().addKill();
            Kit killerKit = sgKiller.getKit();
            if (killerKit != null) {
                killerKit.onPlayerKill(sgKiller, sgVictim);
            }

            String killerKitName = killerKit != null ? killerKit.getDisplayName() : "?";
            String victimKitName = sgVictim.getKit() != null ? sgVictim.getKit().getDisplayName() : "?";
            game.broadcast("&c" + sgKiller.getDisplayName() + " &7[" + killerKitName + "] &ekilled &c" +
                    sgVictim.getDisplayName() + " &7[" + victimKitName + "] &8- &6" +
                    sgKiller.getSessionStats().getKills() + " kill" +
                    (sgKiller.getSessionStats().getKills() != 1 ? "s" : "") + "!");
        } else {
            String victimKitName = sgVictim.getKit() != null ? sgVictim.getKit().getDisplayName() : "?";
            game.broadcast("&c" + sgVictim.getDisplayName() + " &7[" + victimKitName + "] &edied.");
        }

        Set<UUID> assists = game.getCombatTracker().getAssists(victim.getUniqueId(), killerId);
        for (UUID assistId : assists) {
            SGPlayer sgAssist = game.getPlayer(assistId);
            if (sgAssist != null) {
                sgAssist.getSessionStats().addAssist();
            }
        }

        plugin.getSpectatorManager().makeSpectator(sgVictim, game);
        game.getCombatTracker().clearEntries(victim.getUniqueId());

        Bukkit.getScheduler().runTask(plugin.getPlugin(), () -> {
            victim.spigot().respawn();
        });

        BlitzPlayerEliminatedEvent apiEvent = new BlitzPlayerEliminatedEvent(game, victim.getUniqueId(), killerId);
        Bukkit.getPluginManager().callEvent(apiEvent);

        if (game.getAliveCount() <= 1 && game.getState() != GameState.ENDED) {
            game.transition(GameState.ENDED);
        }
    }
}
