package org.rewind.blitzBlitz.core.game.states;

import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.game.GameStateHandler;

public final class WaitingState implements GameStateHandler {

    @Override
    @NotNull
    public GameState getState() { return GameState.WAITING; }

    @Override
    public void onEnter(@NotNull Game game) {
        game.setTimer(0);
        game.broadcast("&eWaiting for players... &7(" + game.getAliveCount() + "/" + game.getPlugin().getBlitzConfig().getMinPlayers() + ")");
    }

    @Override
    public void onTick(@NotNull Game game) {
        int minPlayers = game.getPlugin().getBlitzConfig().getMinPlayers();
        if (game.getAliveCount() >= minPlayers) {
            game.transition(GameState.COUNTDOWN);
        }
    }

    @Override
    public void onExit(@NotNull Game game) {}
}
