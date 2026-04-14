package org.rewind.blitzBlitz.api.events;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.api.BlitzGame;

import java.util.UUID;

public final class BlitzGameEndEvent extends BlitzEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final BlitzGame game;
    private final UUID winner;

    public BlitzGameEndEvent(@NotNull BlitzGame game, @Nullable UUID winner) {
        this.game = game;
        this.winner = winner;
    }

    @NotNull
    public BlitzGame getGame() { return game; }

    @Nullable
    public UUID getWinner() { return winner; }

    @Override
    @NotNull
    public HandlerList getHandlers() { return HANDLER_LIST; }

    public static HandlerList getHandlerList() { return HANDLER_LIST; }
}
