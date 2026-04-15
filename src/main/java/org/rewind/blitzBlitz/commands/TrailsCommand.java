package org.rewind.blitzBlitz.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.util.ChatUtil;

public final class TrailsCommand extends BlitzCommand {

    public TrailsCommand(@NotNull BlitzPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getName() { return "trails"; }

    @Override
    @Nullable
    public String getPermission() { return "blitz.trails"; }

    @Override
    @NotNull
    public String getUsage() { return "/blitz trails"; }

    @Override
    public boolean isPlayerOnly() { return true; }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!checkPermission(sender) || !checkPlayer(sender)) return;
        Player player = asPlayer(sender);

        Game game = plugin.getGameByPlayer(player.getUniqueId());
        if (game == null || game.getState() != GameState.WAITING) {
            ChatUtil.sendMessage(player, "&cYou can only browse trails in the waiting lobby.");
            return;
        }

        plugin.getCosmeticsManager().loadPlayerCosmetics(player.getUniqueId());
        plugin.getCosmeticsGUI().open(player, 0);
    }
}
