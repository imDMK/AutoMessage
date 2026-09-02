package com.github.imdmk.automessage.velocity;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.platform.scheduler.TaskHandle;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;

import java.time.Duration;

public final class VelocityTaskScheduler implements TaskScheduler {

    private final Object plugin;
    private final ProxyServer proxy;

    public VelocityTaskScheduler(Object plugin, ProxyServer proxy) {
        this.plugin = plugin;
        this.proxy = proxy;
    }

    @Override
    public TaskHandle runAsync(Runnable runnable) {
        return handle(proxy.getScheduler()
                .buildTask(plugin, runnable)
                .schedule());
    }

    @Override
    public TaskHandle runLaterSync(Runnable runnable, Duration delay) {
        return handle(proxy.getScheduler()
                .buildTask(plugin, runnable)
                .delay(delay)
                .schedule());
    }

    @Override
    public TaskHandle runTimerSync(PluginTask task) {
        return handle(proxy.getScheduler()
                .buildTask(plugin, (Runnable) task)
                .delay(task.delay())
                .repeat(task.period())
                .schedule());
    }

    @Override
    public void shutdown() {
        // Velocity cancels a plugin's tasks when it unregisters the plugin, and offers no handle
        // to do it early, so there is nothing to do here that would not be done anyway.
    }

    private TaskHandle handle(ScheduledTask task) {
        return task::cancel;
    }
}
