package org.rewind.blitzBlitz.core.game;

import org.jetbrains.annotations.NotNull;

public interface GameStateHandler {

    @NotNull
    GameState getState();

    void onEnter(@NotNull Game game);

    void onTick(@NotNull Game game);

    void onExit(@NotNull Game game);
}
