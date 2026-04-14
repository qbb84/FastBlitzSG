package org.rewind.blitzBlitz.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class BlitzKitActivateEvent extends BlitzEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final UUID playerId;
    private final String kitId;
    private boolean cancelled;

    public BlitzKitActivateEvent(@NotNull UUID playerId, @NotNull String kitId) {
        this.playerId = playerId;
        this.kitId = kitId;
    }

    @NotNull
    public UUID getPlayerId() { return playerId; }

    @NotNull
    public String getKitId() { return kitId; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    @NotNull
    public HandlerList getHandlers() { return HANDLER_LIST; }

    public static HandlerList getHandlerList() { return HANDLER_LIST; }
}
