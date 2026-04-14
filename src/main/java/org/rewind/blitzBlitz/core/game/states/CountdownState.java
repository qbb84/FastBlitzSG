package org.rewind.blitzBlitz.core.game.states;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.game.GameStateHandler;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ChatUtil;

public final class CountdownState implements GameStateHandler {

    private static final int TICK_INTERVAL = 20;

    private BossBar bossBar;
    private int totalCountdown;

    @Override
    @NotNull
    public GameState getState() { return GameState.COUNTDOWN; }

    @Override
    public void onEnter(@NotNull Game game) {
        totalCountdown = game.getPlugin().getBlitzConfig().getCountdownSeconds();
        game.setTimer(totalCountdown);
        bossBar = BossBar.bossBar(
                Component.text("Game starting in " + totalCountdown + "s"),
                1.0f,
                BossBar.Color.GREEN,
                BossBar.Overlay.PROGRESS
        );
        for (SGPlayer sgPlayer : game.getPlayers().values()) {
            Player player = sgPlayer.getBukkitPlayer();
            if (player != null) {
                player.showBossBar(bossBar);
            }
        }
        game.broadcast("&aGame starting in &e" + totalCountdown + " seconds&a!");
    }

    @Override
    public void onTick(@NotNull Game game) {
        int minPlayers = game.getPlugin().getBlitzConfig().getMinPlayers();
        if (game.getAliveCount() < minPlayers) {
            game.broadcast("&cNot enough players! Countdown cancelled.");
            game.transition(GameState.WAITING);
            return;
        }

        game.decrementTimer();
        int remaining = game.getTimer();

        if (remaining <= 0) {
            game.transition(GameState.GRACE_PERIOD);
            return;
        }

        float progress = (float) remaining / totalCountdown;
        bossBar.progress(Math.max(0f, Math.min(1f, progress)));
        bossBar.name(Component.text("Game starting in " + remaining + "s"));

        if (remaining <= 5 || remaining == 10 || remaining == 15 || remaining == 20) {
            game.broadcast("&eGame starting in &c" + remaining + " &esecond" + (remaining != 1 ? "s" : "") + "!");
        }
    }

    @Override
    public void onExit(@NotNull Game game) {
        if (bossBar != null) {
            for (SGPlayer sgPlayer : game.getPlayers().values()) {
                Player player = sgPlayer.getBukkitPlayer();
                if (player != null) {
                    player.hideBossBar(bossBar);
                }
            }
            bossBar = null;
        }
    }
}
