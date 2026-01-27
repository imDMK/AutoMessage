package com.github.imdmk.automessage.platform.scheduler;

import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

public interface TaskScheduler {

    BukkitTask runSync(Runnable runnable);
    BukkitTask runAsync(Runnable runnable);

    BukkitTask runLaterAsync(Runnable runnable, Duration delay);
    BukkitTask runLaterSync(Runnable runnable, Duration delay);

    BukkitTask runTimerSync(Runnable runnable, Duration delay, Duration period);
    BukkitTask runTimerSync(PluginTask task);

    BukkitTask runTimerAsync(Runnable runnable, Duration delay, Duration period);
    BukkitTask runTimerAsync(PluginTask task);

    void cancelTask(int taskId);
    void shutdown();

}
