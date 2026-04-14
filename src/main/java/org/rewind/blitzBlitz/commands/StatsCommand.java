package org.rewind.blitzBlitz.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.player.GameStats;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.UUID;

public final class StatsCommand extends BlitzCommand {

    public StatsCommand(@NotNull BlitzPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getName() { return "stats"; }

    @Override
    @Nullable
    public String getPermission() { return "blitz.stats"; }

    @Override
    @NotNull
    public String getUsage() { return "/blitz stats [player]"; }

    @Override
    public boolean isPlayerOnly() { return false; }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!checkPermission(sender)) return;

        UUID targetUuid;
        String targetName;

        if (args.length > 0) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target != null) {
                targetUuid = target.getUniqueId();
                targetName = target.getName();
            } else {
                sender.sendMessage(ChatUtil.colorize(ChatUtil.PREFIX + "&cPlayer not found: " + args[0]));
                return;
            }
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatUtil.colorize(ChatUtil.PREFIX + "&cSpecify a player name."));
                return;
            }
            targetUuid = player.getUniqueId();
            targetName = player.getName();
        }

        GameStats stats = plugin.getStatsRepository().loadStats(targetUuid);
        if (stats == null) {
            sender.sendMessage(ChatUtil.colorize(ChatUtil.PREFIX + "&cNo stats found for " + targetName));
            return;
        }

        sender.sendMessage(ChatUtil.colorize("&6&l--- Stats for " + targetName + " ---"));
        sender.sendMessage(ChatUtil.colorize("&7Kills: &e" + stats.getKills()));
        sender.sendMessage(ChatUtil.colorize("&7Deaths: &e" + stats.getDeaths()));
        sender.sendMessage(ChatUtil.colorize("&7Wins: &e" + stats.getWins()));
        sender.sendMessage(ChatUtil.colorize("&7Games Played: &e" + stats.getGamesPlayed()));
        sender.sendMessage(ChatUtil.colorize("&7Assists: &e" + stats.getAssists()));
        sender.sendMessage(ChatUtil.colorize("&7Damage Dealt: &e" + String.format("%.1f", stats.getDamageDealt())));
        sender.sendMessage(ChatUtil.colorize("&7Chests Looted: &e" + stats.getChestsLooted()));
        sender.sendMessage(ChatUtil.colorize("&7Blitz Stars Used: &e" + stats.getBlitzStarsUsed()));
    }
}
