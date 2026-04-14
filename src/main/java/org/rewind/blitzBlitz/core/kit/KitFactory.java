package org.rewind.blitzBlitz.core.kit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class KitFactory {

    private final Map<String, Kit> registeredKits = new ConcurrentHashMap<>();

    public void register(@NotNull Kit kit) {
        registeredKits.put(kit.getId().toLowerCase(), kit);
    }

    @Nullable
    public Kit create(@NotNull String id) {
        return registeredKits.get(id.toLowerCase());
    }

    @NotNull
    public Collection<Kit> getAll() {
        return Collections.unmodifiableCollection(registeredKits.values());
    }

    public boolean exists(@NotNull String id) {
        return registeredKits.containsKey(id.toLowerCase());
    }

    @NotNull
    public Collection<String> getKitIds() {
        return Collections.unmodifiableCollection(registeredKits.keySet());
    }
}
