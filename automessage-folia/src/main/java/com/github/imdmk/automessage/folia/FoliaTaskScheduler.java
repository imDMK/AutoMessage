package com.github.imdmk.automessage.folia;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.platform.scheduler.TaskHandle;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.plugin.Plugin;

import java.time.Duration;

public final class FoliaTaskScheduler implements TaskScheduler {

    private static final long MILLIS_PER_TICK = 50L;

    private final Plugin plugin;
    private final GlobalRegionScheduler globalScheduler;
    private final AsyncScheduler asyncScheduler;

    public FoliaTaskScheduler(
            Plugin plugin,
            GlobalRegionScheduler globalScheduler,
            AsyncScheduler asyncScheduler
    ) {
        this.plugin = plugin;
        this.globalScheduler = globalScheduler;
        this.asyncScheduler = asyncScheduler;
    }

    @Override
    public TaskHandle runAsync(Runnable runnable) {
        return asyncScheduler.runNow(plugin, task -> runnable.run())::cancel;
    }

    @Override
    public TaskHandle runLaterSync(Runnable runnable, Duration delay) {
        // Folia refuses a delay of zero ticks, where Bukkit read it as "next tick".
        return globalScheduler.runDelayed(plugin, task -> runnable.run(), atLeastOneTick(delay))::cancel;
    }

    @Override
    public TaskHandle runTimerSync(PluginTask task) {
        return globalScheduler.runAtFixedRate(
                plugin,
                scheduled -> task.run(),
                atLeastOneTick(task.delay()),
                atLeastOneTick(task.period())
        )::cancel;
    }

    @Override
    public void shutdown() {
        globalScheduler.cancelTasks(plugin);
        asyncScheduler.cancelTasks(plugin);
    }

    private static long atLeastOneTick(Duration duration) {
        return Math.max(1L, duration.toMillis() / MILLIS_PER_TICK);
    }
}
