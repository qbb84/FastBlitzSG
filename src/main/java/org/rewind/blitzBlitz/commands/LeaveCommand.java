package org.rewind.blitzBlitz.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.util.ChatUtil;

public final class LeaveCommand extends BlitzCommand {

    public LeaveCommand(@NotNull BlitzPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getName() { return "leave"; }

    @Override
    @Nullable
    public String getPermission() { return "blitz.leave"; }

    @Override
    @NotNull
    public String getUsage() { return "/blitz leave"; }

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

        game.broadcast("&e" + player.getName() + " &chas left the game.");
        game.removePlayer(player.getUniqueId());
        plugin.getPlayerManager().resetPlayer(player);
        ChatUtil.sendMessage(player, "&aYou have left the game.");
    }
}
