package org.rewind.blitzBlitz.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.util.ChatUtil;

public final class ForceStartCommand extends BlitzCommand {

    public ForceStartCommand(@NotNull BlitzPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getName() { return "forcestart"; }

    @Override
    @Nullable
    public String getPermission() { return "blitz.admin"; }

    @Override
    @NotNull
    public String getUsage() { return "/blitz forcestart"; }

    @Override
    public boolean isPlayerOnly() { return true; }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!checkPermission(sender) || !checkPlayer(sender)) return;
        Player player = asPlayer(sender);

        Game game = plugin.getGameByPlayer(player.getUniqueId());
        if (game == null) {
            ChatUtil.sendMessage(player, "&cYou are not in a game.");
            return;
        }

        if (game.getState() == GameState.WAITING || game.getState() == GameState.COUNTDOWN) {
            if (game.getAliveCount() < 2) {
                ChatUtil.sendMessage(player, "&cNeed at least 2 players to force start.");
                return;
            }
            game.getStateMachine().forceState(game, GameState.GRACE_PERIOD);
            game.broadcast("&c&lGame force started by an admin!");
        } else {
            ChatUtil.sendMessage(player, "&cGame is already in progress.");
        }
    }
}
