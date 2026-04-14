package org.rewind.blitzBlitz.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.kit.Kit;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.List;
import java.util.stream.Collectors;

public final class KitCommand extends BlitzCommand {

    public KitCommand(@NotNull BlitzPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getName() { return "kit"; }

    @Override
    @Nullable
    public String getPermission() { return "blitz.kit"; }

    @Override
    @NotNull
    public String getUsage() { return "/blitz kit [name]"; }

    @Override
    public boolean isPlayerOnly() { return true; }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!checkPermission(sender) || !checkPlayer(sender)) return;
        Player player = asPlayer(sender);

        if (args.length == 0) {
            plugin.getGUIManager().openKitSelection(player);
            return;
        }

        String kitId = args[0].toLowerCase();
        Kit kit = plugin.getKitManager().getFactory().create(kitId);
        if (kit == null) {
            ChatUtil.sendMessage(player, "&cKit not found: " + args[0]);
            return;
        }

        plugin.getKitManager().selectKit(player.getUniqueId(), kitId);
        ChatUtil.sendMessage(player, "&aKit selected: &e" + kit.getDisplayName());
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return plugin.getKitManager().getFactory().getKitIds().stream()
                    .filter(id -> id.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
