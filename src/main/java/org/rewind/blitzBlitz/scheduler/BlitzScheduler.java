package org.rewind.blitzBlitz.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BlitzScheduler {

    private static final long TICKS_PER_SECOND = 20L;

    private final JavaPlugin plugin;
    private final Map<String, NamedTask> tasks;

    public BlitzScheduler(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.tasks = new ConcurrentHashMap<>();
    }

    @NotNull
    public NamedTask runTask(@NotNull String name, @NotNull Runnable runnable) {
        BukkitTask bukkit = Bukkit.getScheduler().runTask(plugin, runnable);
        NamedTask namedTask = new NamedTask(name, bukkit);
        tasks.put(name, namedTask);
        return namedTask;
    }

    @NotNull
    public NamedTask runTaskLater(@NotNull String name, @NotNull Runnable runnable, long delayTicks) {
        BukkitTask bukkit = Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
        NamedTask namedTask = new NamedTask(name, bukkit);
        tasks.put(name, namedTask);
        return namedTask;
    }

    @NotNull
    public NamedTask runTaskLaterSeconds(@NotNull String name, @NotNull Runnable runnable, long delaySeconds) {
        return runTaskLater(name, runnable, delaySeconds * TICKS_PER_SECOND);
    }

    @NotNull
    public NamedTask runTaskTimer(@NotNull String name, @NotNull Runnable runnable, long delayTicks, long periodTicks) {
        BukkitTask bukkit = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks);
        NamedTask namedTask = new NamedTask(name, bukkit);
        tasks.put(name, namedTask);
        return namedTask;
    }

    @NotNull
    public NamedTask runTaskAsync(@NotNull String name, @NotNull Runnable runnable) {
        BukkitTask bukkit = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        NamedTask namedTask = new NamedTask(name, bukkit);
        tasks.put(name, namedTask);
        return namedTask;
    }

    public void cancelTask(@NotNull String name) {
        NamedTask task = tasks.remove(name);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    public void cancelAllWithPrefix(@NotNull String prefix) {
        tasks.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(prefix)) {
                if (!entry.getValue().isCancelled()) {
                    entry.getValue().cancel();
                }
                return true;
            }
            return false;
        });
    }

    public void cancelAll() {
        tasks.values().forEach(task -> {
            if (!task.isCancelled()) {
                task.cancel();
            }
        });
        tasks.clear();
    }

    @Nullable
    public NamedTask getTask(@NotNull String name) {
        return tasks.get(name);
    }

    public boolean isTaskActive(@NotNull String name) {
        NamedTask task = tasks.get(name);
        return task != null && !task.isCancelled();
    }
}
