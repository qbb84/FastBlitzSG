package org.rewind.blitzBlitz.cosmetics;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

public final class TrailContext {

    private static final int MAX_HISTORY_SIZE = 5;

    private int tickCount;
    private Location lastLocation;
    private final Deque<Location> positionHistory;
    private boolean sprinting;
    private Vector movementDirection;

    public TrailContext() {
        this.tickCount = 0;
        this.positionHistory = new ArrayDeque<>(MAX_HISTORY_SIZE);
        this.movementDirection = new Vector(0, 0, 0);
    }

    public void update(@NotNull Location currentLocation, boolean sprinting) {
        this.tickCount++;

        if (lastLocation != null && lastLocation.getWorld() != null
                && lastLocation.getWorld().equals(currentLocation.getWorld())) {
            movementDirection = currentLocation.toVector().subtract(lastLocation.toVector());
        } else {
            movementDirection = new Vector(0, 0, 0);
        }

        this.sprinting = sprinting;

        positionHistory.addLast(currentLocation.clone());
        while (positionHistory.size() > MAX_HISTORY_SIZE) {
            positionHistory.removeFirst();
        }

        this.lastLocation = currentLocation.clone();
    }

    public int getTickCount() { return tickCount; }

    @Nullable
    public Location getLastLocation() { return lastLocation; }

    @NotNull
    public Deque<Location> getPositionHistory() { return positionHistory; }

    public boolean isSprinting() { return sprinting; }

    @NotNull
    public Vector getMovementDirection() { return movementDirection; }

    public boolean isMoving() {
        return movementDirection.lengthSquared() > 0.001;
    }

    public void reset() {
        tickCount = 0;
        lastLocation = null;
        positionHistory.clear();
        movementDirection = new Vector(0, 0, 0);
        sprinting = false;
    }
}
