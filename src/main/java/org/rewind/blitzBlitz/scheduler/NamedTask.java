package org.rewind.blitzBlitz.scheduler;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public record NamedTask(@NotNull String name, @NotNull BukkitTask task) {

    public void cancel() {
        task.cancel();
    }

    public boolean isCancelled() {
        return task.isCancelled();
    }
}
