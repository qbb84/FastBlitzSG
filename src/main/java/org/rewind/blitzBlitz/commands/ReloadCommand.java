package org.rewind.blitzBlitz.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.util.ChatUtil;

public final class ReloadCommand extends BlitzCommand {

    public ReloadCommand(@NotNull BlitzPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getName() { return "reload"; }

    @Override
    @Nullable
    public String getPermission() { return "blitz.admin"; }

    @Override
    @NotNull
    public String getUsage() { return "/blitz reload"; }

    @Override
    public boolean isPlayerOnly() { return false; }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!checkPermission(sender)) return;
        plugin.reload();
        sender.sendMessage(ChatUtil.colorize(ChatUtil.PREFIX + "&aConfiguration reloaded successfully."));
    }
}
