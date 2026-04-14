package org.rewind.blitzBlitz.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.TimeUtil;

public final class ScoreboardManager {

    public void update(@NotNull Game game) {
        for (SGPlayer sgPlayer : game.getPlayers().values()) {
            Player player = sgPlayer.getBukkitPlayer();
            if (player == null) continue;

            Scoreboard scoreboard = buildScoreboard(game, sgPlayer);
            player.setScoreboard(scoreboard);

            player.sendPlayerListHeaderAndFooter(
                    Component.text(org.rewind.blitzBlitz.util.ChatUtil.colorize("&6&lBlitz Survival Games")),
                    Component.text(org.rewind.blitzBlitz.util.ChatUtil.colorize(
                            "&7Arena: &e" + game.getArena().getName() + " &8| &7Players: &e" + game.getAliveCount()))
            );
        }
    }

    @NotNull
    private Scoreboard buildScoreboard(@NotNull Game game, @NotNull SGPlayer sgPlayer) {
        SidebarBuilder builder = new SidebarBuilder()
                .title("&6&lBLITZ SG")
                .blank()
                .line("&7State: &f" + formatState(game.getState()));

        if (game.getState() == GameState.GRACE_PERIOD || game.getState() == GameState.DEATHMATCH) {
            builder.line("&7Time: &c" + TimeUtil.formatTime(game.getTimer()));
        } else if (game.getState() == GameState.ACTIVE) {
            builder.line("&7Elapsed: &e" + TimeUtil.formatTime(game.getTimer()));
        }

        builder.blank()
                .line("&7Players Alive: &a" + game.getAliveCount())
                .line("&7Your Kills: &c" + sgPlayer.getSessionStats().getKills())
                .blank();

        if (game.getState() == GameState.ACTIVE && game.getAliveCount() <= 6) {
            builder.line("&e&lDeathmatch soon!");
        }

        String kitName = sgPlayer.getKit() != null ? sgPlayer.getKit().getDisplayName() : "None";
        builder.line("&7Kit: &e" + kitName)
                .blank()
                .line("&6blitzsg.net");

        return builder.build();
    }

    @NotNull
    private String formatState(@NotNull GameState state) {
        return switch (state) {
            case WAITING -> "&eWaiting";
            case COUNTDOWN -> "&aStarting";
            case GRACE_PERIOD -> "&bGrace Period";
            case ACTIVE -> "&cFighting";
            case DEATHMATCH -> "&4Deathmatch";
            case ENDED -> "&6Game Over";
        };
    }
}
