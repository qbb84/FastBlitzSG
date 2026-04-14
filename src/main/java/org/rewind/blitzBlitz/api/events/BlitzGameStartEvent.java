package org.rewind.blitzBlitz.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.api.BlitzGame;

public final class BlitzGameStartEvent extends BlitzEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final BlitzGame game;
    private boolean cancelled;

    public BlitzGameStartEvent(@NotNull BlitzGame game) {
        this.game = game;
    }

    @NotNull
    public BlitzGame getGame() { return game; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    @NotNull
    public HandlerList getHandlers() { return HANDLER_LIST; }

    public static HandlerList getHandlerList() { return HANDLER_LIST; }
}
