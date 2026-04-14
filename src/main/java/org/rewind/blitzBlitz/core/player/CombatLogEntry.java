package org.rewind.blitzBlitz.core.player;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CombatLogEntry(
        @NotNull UUID attacker,
        @NotNull UUID victim,
        double damage,
        long timestamp,
        @NotNull String cause
) {
    public boolean isExpired(long combatTagMillis) {
        return System.currentTimeMillis() - timestamp > combatTagMillis;
    }
}
