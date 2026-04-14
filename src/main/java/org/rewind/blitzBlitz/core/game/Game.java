package org.rewind.blitzBlitz.core.game;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.arena.Arena;
import org.rewind.blitzBlitz.core.arena.ArenaState;
import org.rewind.blitzBlitz.core.player.CombatTracker;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.core.player.SGPlayerState;
import org.rewind.blitzBlitz.api.BlitzGame;
import org.rewind.blitzBlitz.api.BlitzPlayer;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.util.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public final class Game implements BlitzGame {

    private final Arena arena;
    private final BlitzPlugin plugin;
    private final GameStateMachine stateMachine;
    private final Map<UUID, SGPlayer> players;
    private final CombatTracker combatTracker;
    private GameState state;
    private int timer;
    private boolean chestsRefilled;

    public Game(@NotNull Arena arena, @NotNull BlitzPlugin plugin) {
        this.arena = arena;
        this.plugin = plugin;
        this.stateMachine = new GameStateMachine(plugin.getPlugin().getLogger());
        this.players = new ConcurrentHashMap<>();
        this.combatTracker = new CombatTracker(plugin.getBlitzConfig().getCombatTagSeconds());
        this.state = GameState.WAITING;
        this.timer = 0;
        this.chestsRefilled = false;

        arena.setState(ArenaState.IN_USE);
    }

    @NotNull
    public Arena getArena() { return arena; }

    @NotNull
    public BlitzPlugin getPlugin() { return plugin; }

    @NotNull
    public GameStateMachine getStateMachine() { return stateMachine; }

    @NotNull
    public GameState getState() { return state; }

    void setStateInternal(@NotNull GameState state) { this.state = state; }

    public int getTimer() { return timer; }

    public void setTimer(int timer) { this.timer = timer; }

    public void decrementTimer() { this.timer--; }

    public boolean isChefsRefilled() { return chestsRefilled; }

    public void setChefsRefilled(boolean chestsRefilled) { this.chestsRefilled = chestsRefilled; }

    @NotNull
    public CombatTracker getCombatTracker() { return combatTracker; }

    @NotNull
    public Map<UUID, SGPlayer> getPlayers() { return players; }

    public boolean addPlayer(@NotNull Player player) {
        if (state != GameState.WAITING && state != GameState.COUNTDOWN) {
            return false;
        }
        if (players.size() >= arena.getMaxPlayers()) {
            return false;
        }
        SGPlayer sgPlayer = plugin.getPlayerManager().getOrCreate(player.getUniqueId());
        sgPlayer.setState(SGPlayerState.ALIVE);
        players.put(player.getUniqueId(), sgPlayer);
        return true;
    }

    public void removePlayer(@NotNull UUID uuid) {
        SGPlayer sgPlayer = players.remove(uuid);
        if (sgPlayer != null) {
            sgPlayer.setState(SGPlayerState.NOT_IN_GAME);
        }
    }

    @Nullable
    public SGPlayer getPlayer(@NotNull UUID uuid) {
        return players.get(uuid);
    }

    @NotNull
    public List<SGPlayer> getAlivePlayers() {
        return players.values().stream()
                .filter(SGPlayer::isAlive)
                .toList();
    }

    @NotNull
    public List<SGPlayer> getSpectators() {
        return players.values().stream()
                .filter(SGPlayer::isSpectating)
                .toList();
    }

    public int getAliveCount() {
        return (int) players.values().stream().filter(SGPlayer::isAlive).count();
    }

    public void broadcast(@NotNull String message) {
        String formatted = ChatUtil.colorize(ChatUtil.PREFIX + message);
        for (SGPlayer sgPlayer : players.values()) {
            Player player = sgPlayer.getBukkitPlayer();
            if (player != null) {
                player.sendMessage(formatted);
            }
        }
    }

    public void tick() {
        stateMachine.tick(this);
    }

    public boolean transition(@NotNull GameState newState) {
        return stateMachine.transition(this, newState);
    }

    @Override
    @NotNull
    public String getArenaName() { return arena.getName(); }

    @Override
    @NotNull
    public String getStateName() { return state.name(); }

    @Override
    @NotNull
    public Collection<? extends BlitzPlayer> getBlitzPlayers() { return players.values(); }

    @Override
    @Nullable
    public BlitzPlayer getBlitzPlayer(@NotNull UUID uuid) { return players.get(uuid); }

    public void cleanup() {
        for (SGPlayer sgPlayer : players.values()) {
            sgPlayer.setState(SGPlayerState.NOT_IN_GAME);
            Player player = sgPlayer.getBukkitPlayer();
            if (player != null) {
                plugin.getPlayerManager().resetPlayer(player);
            }
        }
        players.clear();
        combatTracker.clear();
        arena.setState(ArenaState.IDLE);
        plugin.getChestManager().clearArenaData(arena.getName());
    }
}
