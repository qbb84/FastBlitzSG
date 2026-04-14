package org.rewind.blitzBlitz.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.arena.Arena;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.List;
import java.util.stream.Collectors;

public final class JoinCommand extends BlitzCommand {

    public JoinCommand(@NotNull BlitzPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getName() { return "join"; }

    @Override
    @Nullable
    public String getPermission() { return "blitz.join"; }

    @Override
    @NotNull
    public String getUsage() { return "/blitz join [arena]"; }

    @Override
    public boolean isPlayerOnly() { return true; }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!checkPermission(sender) || !checkPlayer(sender)) return;
        Player player = asPlayer(sender);

        if (plugin.getGameByPlayer(player.getUniqueId()) != null) {
            ChatUtil.sendMessage(player, "&cYou are already in a game! Use /blitz leave first.");
            return;
        }

        Game game;
        if (args.length > 0) {
            game = plugin.getActiveGames().get(args[0].toLowerCase());
            if (game == null) {
                Arena arena = plugin.getArenaRepository().findByName(args[0]);
                if (arena == null) {
                    ChatUtil.sendMessage(player, "&cArena not found: " + args[0]);
                    return;
                }
                game = plugin.createGame(arena);
            }
        } else {
            game = plugin.findAvailableGame();
            if (game == null) {
                Arena arena = plugin.getArenaRepository().findAvailable();
                if (arena == null) {
                    ChatUtil.sendMessage(player, "&cNo available arenas!");
                    return;
                }
                game = plugin.createGame(arena);
            }
        }

        if (game.getState() != GameState.WAITING && game.getState() != GameState.COUNTDOWN) {
            ChatUtil.sendMessage(player, "&cThat game has already started!");
            return;
        }

        if (game.addPlayer(player)) {
            plugin.getPlayerManager().resetPlayer(player);
            game.broadcast("&e" + player.getName() + " &ahas joined! &7(" + game.getAliveCount() + "/" + game.getArena().getMaxPlayers() + ")");
        } else {
            ChatUtil.sendMessage(player, "&cUnable to join the game (it may be full).");
        }
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return plugin.getArenaRepository().findAll().stream()
                    .map(Arena::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
