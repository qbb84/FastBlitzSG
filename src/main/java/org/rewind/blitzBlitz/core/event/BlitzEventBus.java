package org.rewind.blitzBlitz.core.event;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BlitzEventBus {

    private final Map<Class<?>, List<Consumer<?>>> handlers;
    private final Logger logger;

    public BlitzEventBus(@NotNull Logger logger) {
        this.handlers = new ConcurrentHashMap<>();
        this.logger = logger;
    }

    @SuppressWarnings("unchecked")
    public <T> void subscribe(@NotNull Class<T> eventType, @NotNull Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(@NotNull T event) {
        List<Consumer<?>> list = handlers.get(event.getClass());
        if (list == null) {
            return;
        }
        for (Consumer<?> handler : list) {
            try {
                ((Consumer<T>) handler).accept(event);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error dispatching event " + event.getClass().getSimpleName(), e);
            }
        }
    }

    public void unsubscribeAll(@NotNull Class<?> eventType) {
        handlers.remove(eventType);
    }

    public void clear() {
        handlers.clear();
    }
}
