package org.rewind.blitzBlitz.core.spectator;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.core.player.SGPlayerState;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.List;

public final class SpectatorManager {

    public void makeSpectator(@NotNull SGPlayer sgPlayer, @NotNull Game game) {
        sgPlayer.setState(SGPlayerState.SPECTATING);
        Player player = sgPlayer.getBukkitPlayer();
        if (player == null) return;

        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
        ChatUtil.sendMessage(player, "&7You are now spectating. Right-click a player to spectate their POV.");
    }

    public void spectatePlayer(@NotNull Player spectator, @NotNull Player target) {
        spectator.setSpectatorTarget(target);
        ChatUtil.sendMessage(spectator, "&7Now spectating &e" + target.getName() + "&7. Sneak to stop.");
    }

    public void stopSpectating(@NotNull Player spectator) {
        spectator.setSpectatorTarget(null);
    }

    public void addExternalSpectator(@NotNull Player player, @NotNull Game game) {
        SGPlayer sgPlayer = game.getPlugin().getPlayerManager().getOrCreate(player.getUniqueId());
        sgPlayer.setState(SGPlayerState.SPECTATING);
        game.getPlayers().put(player.getUniqueId(), sgPlayer);
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);

        List<SGPlayer> alive = game.getAlivePlayers();
        if (!alive.isEmpty()) {
            Player target = alive.get(0).getBukkitPlayer();
            if (target != null) {
                player.teleport(target.getLocation());
            }
        }

        ChatUtil.sendMessage(player, "&7You are spectating &e" + game.getArena().getName() + "&7.");
    }

    public void removeSpectator(@NotNull Player player, @NotNull Game game) {
        game.removePlayer(player.getUniqueId());
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setSpectatorTarget(null);
    }
}
