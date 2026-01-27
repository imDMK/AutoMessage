package com.github.imdmk.automessage.platform.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

public final class BukkitTaskScheduler implements TaskScheduler {

    private static final long MILLIS_PER_TICK = 50L;

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public BukkitTaskScheduler(Plugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public BukkitTask runSync(Runnable runnable) {
        return scheduler.runTask(plugin, runnable);
    }

    @Override
    public BukkitTask runAsync(Runnable runnable) {
        return scheduler.runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public BukkitTask runLaterAsync(Runnable runnable, Duration delay) {
        return scheduler.runTaskLaterAsynchronously(plugin, runnable, toTicks(delay));
    }

    @Override
    public BukkitTask runLaterSync(Runnable runnable, Duration delay) {
        return scheduler.runTaskLater(plugin, runnable, toTicks(delay));
    }

    @Override
    public BukkitTask runTimerSync(
            Runnable runnable,
            Duration delay,
            Duration period
    ) {
        return scheduler.runTaskTimer(plugin, runnable, toTicks(delay), toTicks(period));
    }

    @Override
    public BukkitTask runTimerSync(PluginTask task) {
        return runTimerSync(task, task.delay(), task.period());
    }

    @Override
    public BukkitTask runTimerAsync(
            Runnable runnable,
            Duration delay,
            Duration period
    ) {
        return scheduler.runTaskTimerAsynchronously(plugin, runnable, toTicks(delay), toTicks(period));
    }

    @Override
    public BukkitTask runTimerAsync(PluginTask task) {
        return runTimerAsync(task, task.delay(), task.period());
    }

    @Override
    public void cancelTask(int taskId) {
        scheduler.cancelTask(taskId);
    }

    @Override
    public void shutdown() {
        scheduler.cancelTasks(plugin);
    }

    private static int toTicks(Duration duration) {
        final long ticks = duration.toMillis() / MILLIS_PER_TICK;
        return ticks <= 0 ? 0 : (int) ticks;
    }
}
