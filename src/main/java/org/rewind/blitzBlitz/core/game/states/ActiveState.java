package org.rewind.blitzBlitz.core.game.states;

import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.game.GameStateHandler;

public final class ActiveState implements GameStateHandler {

    private static final int DEATHMATCH_TRIGGER_PLAYERS = 4;
    private static final int DEATHMATCH_TRIGGER_TIME = 600;

    @Override
    @NotNull
    public GameState getState() { return GameState.ACTIVE; }

    @Override
    public void onEnter(@NotNull Game game) {
        game.setTimer(0);
        game.broadcast("&c&lPvP is now ENABLED! &eFight to survive!");
        game.getPlugin().getChestManager().refillChests(game.getArena());
        game.setChefsRefilled(true);
    }

    @Override
    public void onTick(@NotNull Game game) {
        game.setTimer(game.getTimer() + 1);

        if (game.getAliveCount() <= 1) {
            game.transition(GameState.ENDED);
            return;
        }

        if (game.getAliveCount() <= DEATHMATCH_TRIGGER_PLAYERS || game.getTimer() >= DEATHMATCH_TRIGGER_TIME) {
            game.transition(GameState.DEATHMATCH);
        }
    }

    @Override
    public void onExit(@NotNull Game game) {}
}
