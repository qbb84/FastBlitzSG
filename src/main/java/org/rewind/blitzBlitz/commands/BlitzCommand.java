package org.rewind.blitzBlitz.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.Collections;
import java.util.List;

public abstract class BlitzCommand {

    protected final BlitzPlugin plugin;

    protected BlitzCommand(@NotNull BlitzPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public abstract String getName();

    @Nullable
    public abstract String getPermission();

    @NotNull
    public abstract String getUsage();

    public abstract boolean isPlayerOnly();

    public abstract void execute(@NotNull CommandSender sender, @NotNull String[] args);

    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return Collections.emptyList();
    }

    protected boolean checkPermission(@NotNull CommandSender sender) {
        String perm = getPermission();
        if (perm != null && !sender.hasPermission(perm)) {
            sender.sendMessage(ChatUtil.colorize(ChatUtil.PREFIX + "&cYou don't have permission to do that."));
            return false;
        }
        return true;
    }

    protected boolean checkPlayer(@NotNull CommandSender sender) {
        if (isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.colorize(ChatUtil.PREFIX + "&cThis command can only be used by players."));
            return false;
        }
        return true;
    }

    @Nullable
    protected Player asPlayer(@NotNull CommandSender sender) {
        return sender instanceof Player p ? p : null;
    }
}
