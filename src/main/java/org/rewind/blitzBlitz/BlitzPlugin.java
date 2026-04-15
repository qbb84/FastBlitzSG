package org.rewind.blitzBlitz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.api.BlitzAPI;
import org.rewind.blitzBlitz.commands.*;
import org.rewind.blitzBlitz.config.BlitzConfig;
import org.rewind.blitzBlitz.config.LootConfig;
import org.rewind.blitzBlitz.config.MessageConfig;
import org.rewind.blitzBlitz.core.arena.Arena;
import org.rewind.blitzBlitz.core.arena.ArenaBuilder;
import org.rewind.blitzBlitz.core.arena.ArenaRepository;
import org.rewind.blitzBlitz.core.arena.InMemoryArenaRepository;
import org.rewind.blitzBlitz.core.chest.ChestManager;
import org.rewind.blitzBlitz.core.chest.ChestTier;
import org.rewind.blitzBlitz.cosmetics.CosmeticsGUI;
import org.rewind.blitzBlitz.cosmetics.CosmeticsManager;
import org.rewind.blitzBlitz.core.event.BlitzEventBus;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.game.states.*;
import org.rewind.blitzBlitz.core.kit.KitFactory;
import org.rewind.blitzBlitz.core.kit.KitManager;
import org.rewind.blitzBlitz.core.kit.kits.*;
import org.rewind.blitzBlitz.core.player.InMemoryPlayerRepository;
import org.rewind.blitzBlitz.core.player.PlayerManager;
import org.rewind.blitzBlitz.core.spectator.SpectatorManager;
import org.rewind.blitzBlitz.gui.GUIManager;
import org.rewind.blitzBlitz.listeners.*;
import org.rewind.blitzBlitz.scheduler.BlitzScheduler;
import org.rewind.blitzBlitz.scoreboard.ScoreboardManager;
import org.rewind.blitzBlitz.stats.FileStatsRepository;
import org.rewind.blitzBlitz.stats.StatsRepository;
import org.rewind.blitzBlitz.util.ChatUtil;
import org.rewind.blitzBlitz.util.LocationUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class BlitzPlugin implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;

    private BlitzConfig blitzConfig;
    private MessageConfig messageConfig;
    private BlitzScheduler scheduler;
    private BlitzEventBus eventBus;
    private ArenaRepository arenaRepository;
    private PlayerManager playerManager;
    private KitFactory kitFactory;
    private KitManager kitManager;
    private ChestManager chestManager;
    private SpectatorManager spectatorManager;
    private ScoreboardManager scoreboardManager;
    private GUIManager guiManager;
    private StatsRepository statsRepository;
    private CosmeticsManager cosmeticsManager;
    private CosmeticsGUI cosmeticsGUI;

    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();
    private final Map<String, BlitzCommand> commands = new LinkedHashMap<>();

    public BlitzPlugin(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        blitzConfig = new BlitzConfig(plugin);
        messageConfig = new MessageConfig(plugin);
        scheduler = new BlitzScheduler(plugin);
        eventBus = new BlitzEventBus(plugin.getLogger());
        arenaRepository = new InMemoryArenaRepository();
        playerManager = new PlayerManager(new InMemoryPlayerRepository());
        statsRepository = new FileStatsRepository(plugin.getDataFolder(), plugin.getLogger());

        kitFactory = new KitFactory();
        registerKits();

        kitManager = new KitManager(plugin, kitFactory);
        chestManager = new ChestManager(blitzConfig);
        spectatorManager = new SpectatorManager();
        scoreboardManager = new ScoreboardManager();
        guiManager = new GUIManager(kitFactory, kitManager);
        cosmeticsManager = new CosmeticsManager(this);
        cosmeticsManager.subscribeEvents(eventBus);
        cosmeticsGUI = new CosmeticsGUI(cosmeticsManager);

        LootConfig.load(plugin, chestManager);
        loadArenas();

        registerCommands();
        registerListeners();
        startGameTickLoop();
        startArmoursmithRepairLoop();
        startCosmeticsTickLoop();

        BlitzAPI.init(this);

        plugin.getLogger().info("BlitzBlitz enabled successfully!");
    }

    public void disable() {
        kitManager.saveSelections();
        cosmeticsManager.saveAll();
        scheduler.cancelAll();
        for (Game game : activeGames.values()) {
            game.cleanup();
        }
        activeGames.clear();
        plugin.getLogger().info("BlitzBlitz disabled.");
    }

    public void reload() {
        plugin.reloadConfig();
        blitzConfig = new BlitzConfig(plugin);
        messageConfig = new MessageConfig(plugin);
        chestManager = new ChestManager(blitzConfig);
        LootConfig.load(plugin, chestManager);
        loadArenas();
        plugin.getLogger().info("BlitzBlitz configuration reloaded.");
    }

    private void registerKits() {
        kitFactory.register(new ArmoursmithKit());
        kitFactory.register(new ArcherKit());
        kitFactory.register(new TamerKit());
        kitFactory.register(new RunnerKit());
        kitFactory.register(new PyroKit());
        kitFactory.register(new AssassinKit());
        kitFactory.register(new BarbarianKit());
        kitFactory.register(new NecromancerKit());
    }

    private void registerCommands() {
        commands.put("join", new JoinCommand(this));
        commands.put("leave", new LeaveCommand(this));
        commands.put("kit", new KitCommand(this));
        commands.put("spectate", new SpectateCommand(this));
        commands.put("stats", new StatsCommand(this));
        commands.put("arena", new ArenaCommand(this));
        commands.put("reload", new ReloadCommand(this));
        commands.put("forcestart", new ForceStartCommand(this));
        commands.put("trails", new TrailsCommand(this));

        var cmd = plugin.getCommand("blitz");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
    }

    private void registerListeners() {
        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerDamageListener(this), plugin);
        pm.registerEvents(new PlayerDeathListener(this), plugin);
        pm.registerEvents(new PlayerInteractListener(this), plugin);
        pm.registerEvents(new InventoryClickListener(this), plugin);
        pm.registerEvents(new PlayerQuitListener(this), plugin);
        pm.registerEvents(new EntityTargetListener(this), plugin);
    }

    private void startGameTickLoop() {
        scheduler.runTaskTimer("game-tick", () -> {
            for (Game game : activeGames.values()) {
                game.tick();
                scoreboardManager.update(game);
            }
        }, 0L, 20L);
    }

    private void startCosmeticsTickLoop() {
        scheduler.runTaskTimer("cosmetics-tick", () -> cosmeticsManager.onTick(), 0L, 2L);
    }

    private void startArmoursmithRepairLoop() {
        scheduler.runTaskTimer("armoursmith-repair", () -> {
            for (Game game : activeGames.values()) {
                for (var sgPlayer : game.getAlivePlayers()) {
                    if (sgPlayer.getKit() instanceof ArmoursmithKit) {
                        ArmoursmithKit.tickRepair(sgPlayer);
                    }
                }
            }
        }, 200L, 200L);
    }

    @NotNull
    public Game createGame(@NotNull Arena arena) {
        Game game = new Game(arena, this);

        game.getStateMachine().registerHandler(new WaitingState());
        game.getStateMachine().registerHandler(new CountdownState());
        game.getStateMachine().registerHandler(new GracePeriodState());
        game.getStateMachine().registerHandler(new ActiveState());
        game.getStateMachine().registerHandler(new DeathmatchState());
        game.getStateMachine().registerHandler(new EndedState());

        game.getStateMachine().forceState(game, GameState.WAITING);
        activeGames.put(arena.getName().toLowerCase(), game);
        return game;
    }

    @Nullable
    public Game findAvailableGame() {
        for (Game game : activeGames.values()) {
            if (game.getState() == GameState.WAITING || game.getState() == GameState.COUNTDOWN) {
                if (game.getAliveCount() < game.getArena().getMaxPlayers()) {
                    return game;
                }
            }
        }
        return null;
    }

    @Nullable
    public Game getGameByPlayer(@NotNull UUID playerId) {
        for (Game game : activeGames.values()) {
            if (game.getPlayer(playerId) != null) {
                return game;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                              @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatUtil.colorize(ChatUtil.PREFIX + "&eUse /blitz help for commands."));
            return true;
        }

        String sub = args[0].toLowerCase();
        BlitzCommand blitzCmd = commands.get(sub);
        if (blitzCmd == null) {
            sender.sendMessage(ChatUtil.colorize(ChatUtil.PREFIX + "&cUnknown command: " + sub));
            return true;
        }

        String[] subArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
        blitzCmd.execute(sender, subArgs);
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                       @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return commands.keySet().stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length > 1) {
            BlitzCommand blitzCmd = commands.get(args[0].toLowerCase());
            if (blitzCmd != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return blitzCmd.tabComplete(sender, subArgs);
            }
        }
        return List.of();
    }

    public void saveArenaConfig(@NotNull Arena arena) {
        File file = new File(plugin.getDataFolder(), "arenas.yml");
        FileConfiguration cfg;
        if (file.exists()) {
            cfg = YamlConfiguration.loadConfiguration(file);
        } else {
            cfg = new YamlConfiguration();
        }

        String path = "arenas." + arena.getName();
        cfg.set(path + ".world", arena.getWorldName());

        List<String> spawns = new ArrayList<>();
        for (Location loc : arena.getSpawnPedestals()) {
            if (loc != null) spawns.add(LocationUtil.serialize(loc));
        }
        cfg.set(path + ".spawns", spawns);

        if (arena.getCentre() != null) {
            cfg.set(path + ".centre", LocationUtil.serialize(arena.getCentre()));
        }
        if (arena.getDeathmatchCentre() != null) {
            cfg.set(path + ".deathmatch-centre", LocationUtil.serialize(arena.getDeathmatchCentre()));
        }
        cfg.set(path + ".deathmatch-radius", arena.getDeathmatchRadius());

        Map<String, String> chestMap = new LinkedHashMap<>();
        int idx = 0;
        for (var entry : arena.getChestLocations().entrySet()) {
            cfg.set(path + ".chests." + idx + ".location", LocationUtil.serialize(entry.getKey()));
            cfg.set(path + ".chests." + idx + ".tier", entry.getValue().name());
            idx++;
        }

        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("[Arena] Failed to save arena config: " + e.getMessage());
        }
    }

    private void loadArenas() {
        File file = new File(plugin.getDataFolder(), "arenas.yml");
        if (!file.exists()) return;

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection arenasSection = cfg.getConfigurationSection("arenas");
        if (arenasSection == null) return;

        for (String name : arenasSection.getKeys(false)) {
            ConfigurationSection section = arenasSection.getConfigurationSection(name);
            if (section == null) continue;

            String world = section.getString("world", "world");
            ArenaBuilder builder = new ArenaBuilder().name(name).world(world);

            List<String> spawns = section.getStringList("spawns");
            for (String spawnStr : spawns) {
                Location loc = LocationUtil.deserialize(spawnStr);
                if (loc != null) builder.addSpawn(loc);
            }

            String centreStr = section.getString("centre");
            if (centreStr != null) {
                Location loc = LocationUtil.deserialize(centreStr);
                if (loc != null) builder.centre(loc);
            }

            String dmCentreStr = section.getString("deathmatch-centre");
            if (dmCentreStr != null) {
                Location loc = LocationUtil.deserialize(dmCentreStr);
                if (loc != null) builder.deathmatchCentre(loc);
            }

            builder.deathmatchRadius(section.getDouble("deathmatch-radius", 50.0));

            ConfigurationSection chestsSection = section.getConfigurationSection("chests");
            if (chestsSection != null) {
                for (String chestKey : chestsSection.getKeys(false)) {
                    ConfigurationSection chestSec = chestsSection.getConfigurationSection(chestKey);
                    if (chestSec == null) continue;
                    String locStr = chestSec.getString("location");
                    String tierStr = chestSec.getString("tier", "COMMON");
                    if (locStr != null) {
                        Location loc = LocationUtil.deserialize(locStr);
                        if (loc != null) {
                            try {
                                ChestTier tier = ChestTier.valueOf(tierStr.toUpperCase());
                                builder.addChest(loc, tier);
                            } catch (IllegalArgumentException ignored) {}
                        }
                    }
                }
            }

            Arena arena = builder.build();
            arenaRepository.save(arena);
            plugin.getLogger().info("[Arena] Loaded arena: " + name);
        }
    }

    @NotNull public JavaPlugin getPlugin() { return plugin; }
    @NotNull public BlitzConfig getBlitzConfig() { return blitzConfig; }
    @NotNull public MessageConfig getMessageConfig() { return messageConfig; }
    @NotNull public BlitzScheduler getScheduler() { return scheduler; }
    @NotNull public BlitzEventBus getEventBus() { return eventBus; }
    @NotNull public ArenaRepository getArenaRepository() { return arenaRepository; }
    @NotNull public PlayerManager getPlayerManager() { return playerManager; }
    @NotNull public KitFactory getKitFactory() { return kitFactory; }
    @NotNull public KitManager getKitManager() { return kitManager; }
    @NotNull public ChestManager getChestManager() { return chestManager; }
    @NotNull public SpectatorManager getSpectatorManager() { return spectatorManager; }
    @NotNull public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    @NotNull public GUIManager getGUIManager() { return guiManager; }
    @NotNull public StatsRepository getStatsRepository() { return statsRepository; }
    @NotNull public Map<String, Game> getActiveGames() { return activeGames; }
    @NotNull public CosmeticsManager getCosmeticsManager() { return cosmeticsManager; }
    @NotNull public CosmeticsGUI getCosmeticsGUI() { return cosmeticsGUI; }
}
