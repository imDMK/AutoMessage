package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.config.ConfigReloadListener;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.time.DurationFormatter;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitTask;

/**
 * Owns the repeating task that dispatches scheduled messages.
 *
 * <p>
 * Bukkit captures the delay and the period when a task is scheduled, so a changed interval
 * cannot take effect while the task is running. Reloading the configuration therefore cancels
 * the current task and schedules a new one with the freshly read timing.
 * </p>
 *
 * <p>
 * The tick itself runs on the main thread: it reads the online player collection and evaluates
 * the audience rules, and neither is safe to touch asynchronously — the player view is backed by
 * a list the server mutates on join and quit, and permission lookups race with attachment
 * changes. Only the delivery of each notice is handed off the main thread, by
 * {@link com.github.imdmk.automessage.scheduled.ScheduledMessageSender#sendAsync}, so the tick
 * costs one pass over the online players and nothing else.
 * </p>
 */
public final class MessageDispatcherService implements ConfigReloadListener {

    private final PluginLogger logger;
    private final Server server;
    private final TaskScheduler taskScheduler;
    private final MessageDispatcherConfig dispatcherConfig;
    private final MessageDispatcher messageDispatcher;

    private BukkitTask task;

    public MessageDispatcherService(
            PluginLogger logger,
            Server server,
            TaskScheduler taskScheduler,
            MessageDispatcherConfig dispatcherConfig,
            MessageDispatcher messageDispatcher
    ) {
        this.logger = logger;
        this.server = server;
        this.taskScheduler = taskScheduler;
        this.dispatcherConfig = dispatcherConfig;
        this.messageDispatcher = messageDispatcher;
    }

    public synchronized void start() {
        if (task != null) {
            return;
        }

        final DispatchTiming timing = DispatchTiming.from(dispatcherConfig, logger);
        final MessageDispatcherTask dispatcherTask = new MessageDispatcherTask(
                server,
                dispatcherConfig,
                messageDispatcher,
                timing
        );

        this.task = taskScheduler.runTimerSync(dispatcherTask);

        logger.info(
                "Scheduled automatic messages every %s, first one in %s.",
                DurationFormatter.format(timing.period()),
                DurationFormatter.format(timing.initialDelay())
        );
    }

    public synchronized void stop() {
        if (task == null) {
            return;
        }

        task.cancel();
        this.task = null;
    }

    public synchronized void restart() {
        stop();
        start();
    }

    @Override
    public void onConfigReload() {
        restart();
    }
}
