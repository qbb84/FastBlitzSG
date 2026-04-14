package org.rewind.blitzBlitz.api.events;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.api.BlitzGame;

import java.util.UUID;

public final class BlitzPlayerEliminatedEvent extends BlitzEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final BlitzGame game;
    private final UUID eliminatedPlayer;
    private final UUID killer;

    public BlitzPlayerEliminatedEvent(@NotNull BlitzGame game, @NotNull UUID eliminatedPlayer, @Nullable UUID killer) {
        this.game = game;
        this.eliminatedPlayer = eliminatedPlayer;
        this.killer = killer;
    }

    @NotNull
    public BlitzGame getGame() { return game; }

    @NotNull
    public UUID getEliminatedPlayer() { return eliminatedPlayer; }

    @Nullable
    public UUID getKiller() { return killer; }

    @Override
    @NotNull
    public HandlerList getHandlers() { return HANDLER_LIST; }

    public static HandlerList getHandlerList() { return HANDLER_LIST; }
}
