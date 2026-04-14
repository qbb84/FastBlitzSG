package org.rewind.blitzBlitz.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.arena.Arena;
import org.rewind.blitzBlitz.core.arena.ArenaSetupSession;
import org.rewind.blitzBlitz.core.chest.ChestTier;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.*;
import java.util.stream.Collectors;

public final class ArenaCommand extends BlitzCommand {

    private final Map<UUID, ArenaSetupSession> sessions = new HashMap<>();

    public ArenaCommand(@NotNull BlitzPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getName() { return "arena"; }

    @Override
    @Nullable
    public String getPermission() { return "blitz.admin"; }

    @Override
    @NotNull
    public String getUsage() { return "/blitz arena <create|setspawn|setchest|setdeathmatch|save|list>"; }

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

        String sub = args[0].toLowerCase();
        String[] subArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];

        switch (sub) {
            case "create" -> handleCreate(player, subArgs);
            case "setspawn" -> handleSetSpawn(player, subArgs);
            case "setchest" -> handleSetChest(player, subArgs);
            case "setdeathmatch" -> handleSetDeathmatch(player);
            case "save" -> handleSave(player);
            case "list" -> handleList(player);
            default -> ChatUtil.sendMessage(player, "&cUnknown sub-command: " + sub);
        }
    }

    private void handleCreate(@NotNull Player player, @NotNull String[] args) {
        if (args.length == 0) {
            ChatUtil.sendMessage(player, "&cUsage: /blitz arena create <name>");
            return;
        }
        String name = args[0];
        if (plugin.getArenaRepository().findByName(name) != null) {
            ChatUtil.sendMessage(player, "&cArena '" + name + "' already exists!");
            return;
        }

        ArenaSetupSession session = new ArenaSetupSession(player.getUniqueId(), name, player.getWorld().getName());
        session.setCentre(player.getLocation());
        sessions.put(player.getUniqueId(), session);
        ChatUtil.sendMessage(player, "&aArena setup started for '&e" + name + "&a'. Centre set to your location.");
        ChatUtil.sendMessage(player, "&7Use &e/blitz arena setspawn <index> &7to add spawn points.");
    }

    private void handleSetSpawn(@NotNull Player player, @NotNull String[] args) {
        ArenaSetupSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            ChatUtil.sendMessage(player, "&cNo active arena setup session. Use /blitz arena create <name> first.");
            return;
        }
        if (args.length == 0) {
            ChatUtil.sendMessage(player, "&cUsage: /blitz arena setspawn <index>");
            return;
        }

        int index;
        try {
            index = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ChatUtil.sendMessage(player, "&cInvalid index: " + args[0]);
            return;
        }

        session.addSpawn(player.getLocation());
        ChatUtil.sendMessage(player, "&aSpawn pedestal &e#" + index + " &aset to your location.");
    }

    private void handleSetChest(@NotNull Player player, @NotNull String[] args) {
        ArenaSetupSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            ChatUtil.sendMessage(player, "&cNo active arena setup session.");
            return;
        }
        if (args.length == 0) {
            ChatUtil.sendMessage(player, "&cUsage: /blitz arena setchest <tier>");
            return;
        }

        ChestTier tier;
        try {
            tier = ChestTier.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            ChatUtil.sendMessage(player, "&cInvalid tier: " + args[0] + ". Valid: COMMON, UNCOMMON, RARE, CENTRE");
            return;
        }

        Block target = player.getTargetBlockExact(5);
        if (target == null || !(target.getState() instanceof org.bukkit.block.Chest)) {
            ChatUtil.sendMessage(player, "&cYou must be looking at a chest!");
            return;
        }

        session.addChest(target.getLocation(), tier);
        ChatUtil.sendMessage(player, "&aChest tagged as &e" + tier.name() + " &aat " + formatLocation(target.getLocation()));
    }

    private void handleSetDeathmatch(@NotNull Player player) {
        ArenaSetupSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            ChatUtil.sendMessage(player, "&cNo active arena setup session.");
            return;
        }
        session.setDeathmatchCentre(player.getLocation());
        ChatUtil.sendMessage(player, "&aDeathmatch centre set to your location.");
    }

    private void handleSave(@NotNull Player player) {
        ArenaSetupSession session = sessions.remove(player.getUniqueId());
        if (session == null) {
            ChatUtil.sendMessage(player, "&cNo active arena setup session.");
            return;
        }

        Arena arena = session.build();
        plugin.getArenaRepository().save(arena);
        plugin.saveArenaConfig(arena);
        ChatUtil.sendMessage(player, "&aArena '&e" + arena.getName() + "&a' saved successfully!");
    }

    private void handleList(@NotNull Player player) {
        Collection<Arena> arenas = plugin.getArenaRepository().findAll();
        if (arenas.isEmpty()) {
            ChatUtil.sendMessage(player, "&7No arenas configured.");
            return;
        }
        ChatUtil.sendMessage(player, "&6&l--- Arenas ---");
        for (Arena arena : arenas) {
            ChatUtil.sendMessage(player, "&e" + arena.getName() + " &7[" + arena.getState().name() + "] &7Spawns: " + arena.getSpawnPedestals().size());
        }
    }

    private String formatLocation(@NotNull Location loc) {
        return String.format("(%d, %d, %d)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("create", "setspawn", "setchest", "setdeathmatch", "save", "list").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setchest")) {
                return Arrays.stream(ChestTier.values())
                        .map(t -> t.name().toLowerCase())
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }
}
