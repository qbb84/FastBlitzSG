package org.rewind.blitzBlitz.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.arena.Arena;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.List;
import java.util.stream.Collectors;

public final class SpectateCommand extends BlitzCommand {

    public SpectateCommand(@NotNull BlitzPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getName() { return "spectate"; }

    @Override
    @Nullable
    public String getPermission() { return "blitz.spectate"; }

    @Override
    @NotNull
    public String getUsage() { return "/blitz spectate [arena]"; }

    @Override
    public boolean isPlayerOnly() { return true; }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!checkPermission(sender) || !checkPlayer(sender)) return;
        Player player = asPlayer(sender);

        if (args.length == 0) {
            ChatUtil.sendMessage(player, "&cUsage: " + getUsage());
            return;
        }

        Game game = plugin.getActiveGames().get(args[0].toLowerCase());
        if (game == null) {
            ChatUtil.sendMessage(player, "&cNo active game in arena: " + args[0]);
            return;
        }

        plugin.getSpectatorManager().addExternalSpectator(player, game);
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return plugin.getActiveGames().keySet().stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
