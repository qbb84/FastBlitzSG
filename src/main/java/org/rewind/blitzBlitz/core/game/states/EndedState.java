package org.rewind.blitzBlitz.core.game.states;

import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.game.GameStateHandler;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class EndedState implements GameStateHandler {

    @Override
    @NotNull
    public GameState getState() { return GameState.ENDED; }

    @Override
    public void onEnter(@NotNull Game game) {
        int delay = game.getPlugin().getBlitzConfig().getEndedDelaySeconds();
        game.setTimer(delay);

        List<SGPlayer> winners = game.getAlivePlayers();
        if (!winners.isEmpty()) {
            SGPlayer winner = winners.get(0);
            String winnerName = winner.getDisplayName();
            String kitName = winner.getKit() != null ? winner.getKit().getDisplayName() : "None";

            ChatUtil.broadcast("&6&l★ &e" + winnerName + " &6has won the game as &e" + kitName + " &6with &c" +
                    winner.getSessionStats().getKills() + " kills&6! &6&l★");

            winner.getSessionStats().addWin();

            Player player = winner.getBukkitPlayer();
            if (player != null) {
                spawnFireworks(player.getLocation());
            }
        } else {
            ChatUtil.broadcast("&eThe game has ended with no winner!");
        }

        for (SGPlayer sgPlayer : game.getPlayers().values()) {
            sgPlayer.getSessionStats().addGamePlayed();
            game.getPlugin().getStatsRepository().saveStats(sgPlayer.getUuid(), sgPlayer.getSessionStats());
        }
    }

    @Override
    public void onTick(@NotNull Game game) {
        game.decrementTimer();

        List<SGPlayer> winners = game.getAlivePlayers();
        if (!winners.isEmpty() && game.getTimer() % 2 == 0) {
            Player player = winners.get(0).getBukkitPlayer();
            if (player != null) {
                spawnFireworks(player.getLocation());
            }
        }

        if (game.getTimer() <= 0) {
            game.cleanup();
        }
    }

    @Override
    public void onExit(@NotNull Game game) {}

    private void spawnFireworks(@NotNull Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE, Color.ORANGE, Color.AQUA};
        meta.addEffect(FireworkEffect.builder()
                .withColor(colors[random.nextInt(colors.length)])
                .withFade(colors[random.nextInt(colors.length)])
                .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)])
                .trail(true)
                .flicker(true)
                .build());
        meta.setPower(1);
        firework.setFireworkMeta(meta);
    }
}
