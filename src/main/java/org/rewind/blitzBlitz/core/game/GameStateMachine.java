package org.rewind.blitzBlitz.core.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public final class GameStateMachine {

    private static final Map<GameState, Set<GameState>> VALID_TRANSITIONS = new EnumMap<>(GameState.class);

    static {
        VALID_TRANSITIONS.put(GameState.WAITING, Set.of(GameState.COUNTDOWN));
        VALID_TRANSITIONS.put(GameState.COUNTDOWN, Set.of(GameState.GRACE_PERIOD, GameState.WAITING));
        VALID_TRANSITIONS.put(GameState.GRACE_PERIOD, Set.of(GameState.ACTIVE));
        VALID_TRANSITIONS.put(GameState.ACTIVE, Set.of(GameState.DEATHMATCH, GameState.ENDED));
        VALID_TRANSITIONS.put(GameState.DEATHMATCH, Set.of(GameState.ENDED));
        VALID_TRANSITIONS.put(GameState.ENDED, Set.of(GameState.WAITING));
    }

    private final Map<GameState, GameStateHandler> handlers;
    private final Logger logger;
    private GameStateHandler currentHandler;

    public GameStateMachine(@NotNull Logger logger) {
        this.handlers = new EnumMap<>(GameState.class);
        this.logger = logger;
    }

    public void registerHandler(@NotNull GameStateHandler handler) {
        handlers.put(handler.getState(), handler);
    }

    public boolean transition(@NotNull Game game, @NotNull GameState newState) {
        GameState currentState = game.getState();
        Set<GameState> allowed = VALID_TRANSITIONS.get(currentState);

        if (allowed == null || !allowed.contains(newState)) {
            logger.warning("[StateMachine] Invalid transition: " + currentState + " -> " + newState);
            return false;
        }

        if (currentHandler != null) {
            currentHandler.onExit(game);
        }

        game.setStateInternal(newState);
        currentHandler = handlers.get(newState);

        if (currentHandler != null) {
            currentHandler.onEnter(game);
        }

        logger.info("[StateMachine] Transitioned: " + currentState + " -> " + newState + " (Arena: " + game.getArena().getName() + ")");
        return true;
    }

    public void tick(@NotNull Game game) {
        if (currentHandler != null) {
            currentHandler.onTick(game);
        }
    }

    @Nullable
    public GameStateHandler getCurrentHandler() {
        return currentHandler;
    }

    public void forceState(@NotNull Game game, @NotNull GameState state) {
        if (currentHandler != null) {
            currentHandler.onExit(game);
        }
        game.setStateInternal(state);
        currentHandler = handlers.get(state);
        if (currentHandler != null) {
            currentHandler.onEnter(game);
        }
    }
}
