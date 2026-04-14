package org.rewind.blitzBlitz.core.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class CombatTracker {

    private static final long DEFAULT_COMBAT_TAG_MILLIS = 15_000L;

    private final long combatTagMillis;
    private final Map<UUID, List<CombatLogEntry>> entries;

    public CombatTracker(int combatTagSeconds) {
        this.combatTagMillis = combatTagSeconds * 1000L;
        this.entries = new ConcurrentHashMap<>();
    }

    public void recordDamage(@NotNull UUID attacker, @NotNull UUID victim, double damage, @NotNull String cause) {
        CombatLogEntry entry = new CombatLogEntry(attacker, victim, damage, System.currentTimeMillis(), cause);
        entries.computeIfAbsent(victim, k -> new ArrayList<>()).add(entry);
    }

    @Nullable
    public UUID getLastAttacker(@NotNull UUID victim) {
        List<CombatLogEntry> list = entries.get(victim);
        if (list == null || list.isEmpty()) {
            return null;
        }
        long now = System.currentTimeMillis();
        for (int i = list.size() - 1; i >= 0; i--) {
            CombatLogEntry entry = list.get(i);
            if (!entry.isExpired(combatTagMillis)) {
                return entry.attacker();
            }
        }
        return null;
    }

    @NotNull
    public Set<UUID> getAssists(@NotNull UUID victim, @Nullable UUID killer) {
        Set<UUID> assists = new HashSet<>();
        List<CombatLogEntry> list = entries.get(victim);
        if (list == null) {
            return assists;
        }
        long now = System.currentTimeMillis();
        for (CombatLogEntry entry : list) {
            if (!entry.isExpired(combatTagMillis) && !entry.attacker().equals(victim)) {
                if (killer == null || !entry.attacker().equals(killer)) {
                    assists.add(entry.attacker());
                }
            }
        }
        return assists;
    }

    public void clearEntries(@NotNull UUID player) {
        entries.remove(player);
    }

    public void clear() {
        entries.clear();
    }
}
