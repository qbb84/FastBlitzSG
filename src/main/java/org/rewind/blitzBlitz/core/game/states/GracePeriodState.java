package org.rewind.blitzBlitz.core.game.states;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.game.GameStateHandler;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.List;

public final class GracePeriodState implements GameStateHandler {

    @Override
    @NotNull
    public GameState getState() { return GameState.GRACE_PERIOD; }

    @Override
    public void onEnter(@NotNull Game game) {
        int duration = game.getPlugin().getBlitzConfig().getGracePeriodSeconds();
        game.setTimer(duration);
        game.broadcast("&a&lGAME STARTED! &ePvP is disabled for &c" + duration + " seconds&e. Loot chests now!");

        List<Location> spawns = game.getArena().getSpawnPedestals();
        List<SGPlayer> alivePlayers = game.getAlivePlayers();
        for (int i = 0; i < alivePlayers.size() && i < spawns.size(); i++) {
            Player player = alivePlayers.get(i).getBukkitPlayer();
            if (player != null && spawns.get(i) != null) {
                player.teleport(spawns.get(i));
            }
        }

        for (SGPlayer sgPlayer : alivePlayers) {
            game.getPlugin().getKitManager().applyKit(sgPlayer);
        }

        game.getPlugin().getChestManager().fillChests(game.getArena());
    }

    @Override
    public void onTick(@NotNull Game game) {
        game.decrementTimer();
        int remaining = game.getTimer();

        for (SGPlayer sgPlayer : game.getAlivePlayers()) {
            Player player = sgPlayer.getBukkitPlayer();
            if (player != null) {
                ChatUtil.sendActionBar(player, "&e&lGrace Period &7- &c" + remaining + "s &7remaining");
            }
        }

        if (remaining <= 0) {
            game.transition(GameState.ACTIVE);
        }
    }

    @Override
    public void onExit(@NotNull Game game) {}
}
