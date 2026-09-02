package com.github.imdmk.automessage.bukkit;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.platform.scheduler.TaskHandle;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

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
    public TaskHandle runAsync(Runnable runnable) {
        return handleOf(scheduler.runTaskAsynchronously(plugin, runnable));
    }

    @Override
    public TaskHandle runLaterSync(Runnable runnable, Duration delay) {
        return handleOf(scheduler.runTaskLater(plugin, runnable, toDelayTicks(delay)));
    }

    @Override
    public TaskHandle runTimerSync(PluginTask task) {
        return handleOf(scheduler.runTaskTimer(
                plugin,
                task,
                toDelayTicks(task.delay()),
                toPeriodTicks(task.period())
        ));
    }

    @Override
    public void shutdown() {
        scheduler.cancelTasks(plugin);
    }

    private static TaskHandle handleOf(org.bukkit.scheduler.BukkitTask task) {
        return task::cancel;
    }

    private static long toDelayTicks(Duration duration) {
        return Math.max(0L, toTicks(duration));
    }

    // Bukkit reads a period of zero ticks as "every tick", which turns a misconfigured interval
    // into a flood of messages.
    private static long toPeriodTicks(Duration duration) {
        return Math.max(1L, toTicks(duration));
    }

    private static long toTicks(Duration duration) {
        return duration.toMillis() / MILLIS_PER_TICK;
    }
}
