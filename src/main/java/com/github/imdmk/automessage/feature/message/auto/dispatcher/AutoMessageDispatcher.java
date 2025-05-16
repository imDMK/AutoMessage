package com.github.imdmk.automessage.feature.message.auto.dispatcher;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.configuration.ConfigurationManager;
import com.github.imdmk.automessage.feature.message.MessageService;
import com.github.imdmk.automessage.feature.message.auto.AutoMessageConfiguration;
import com.github.imdmk.automessage.scheduler.TaskScheduler;
import com.github.imdmk.automessage.util.CollectionUtil;
import com.github.imdmk.automessage.util.DurationUtil;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dispatches automatic messages to online players based on configured strategy.
 */
public final class AutoMessageDispatcher {

    private final ConfigurationManager configurationManager;
    private final AutoMessageConfiguration configuration;
    private final MessageService messageService;
    private final TaskScheduler taskScheduler;

    private final AtomicInteger position = new AtomicInteger(0);

    private volatile long delayTicks;
    private volatile int currentTask;

    public AutoMessageDispatcher(
            @NotNull ConfigurationManager configurationManager,
            @NotNull AutoMessageConfiguration configuration,
            @NotNull MessageService messageService,
            @NotNull TaskScheduler taskScheduler
    ) {
        this.configurationManager = Objects.requireNonNull(configurationManager, "configurationManager cannot be null");
        this.configuration = Objects.requireNonNull(configuration, "configuration cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.taskScheduler = Objects.requireNonNull(taskScheduler, "taskScheduler cannot be null");

        this.delayTicks = DurationUtil.toTicks(this.configuration.delay);
    }

    /**
     * Selects and dispatches a message to all online players according to the auto message mode specified in the configuration.
     */
    public void dispatch() {
        Notice message = this.selectMessage();

        this.messageService.create()
                .onlinePlayers()
                .notice(message)
                .sendAsync();
    }

    /**
     * Dispatches a specific message to all online players.
     *
     * @param message the message to dispatch
     */
    public void dispatch(@NotNull Notice message) {
        this.messageService.create()
                .onlinePlayers()
                .notice(message)
                .sendAsync();
    }

    /**
     * Changes the delay between dispatched messages and restarts the scheduled task.
     *
     * @param newDelayTicks new delay in ticks; must be positive
     * @throws IllegalArgumentException if newDelayTicks <= 0
     */
    public void changeDelay(long newDelayTicks) {
        if (newDelayTicks <= 0) {
            throw new IllegalArgumentException("Delay must be positive");
        }

        if (this.delayTicks == newDelayTicks) {
            return; // no change
        }

        this.configuration.delay = DurationUtil.fromTicks(newDelayTicks);
        this.configurationManager.save(this.configuration);

        this.delayTicks = newDelayTicks;
        this.schedule();
    }

    /**
     * Schedules or reschedules the repeating task that dispatches automatic messages
     * at fixed intervals defined by {@link #delayTicks}.
     * <p>
     * If a previous task is already scheduled, it will be canceled before scheduling a new one.
     * This method is synchronized to avoid concurrent scheduling issues.
     * </p>
     */
    public synchronized void schedule() {
        if (this.currentTask > 0) {
            this.taskScheduler.cancelTask(this.currentTask);
        }

        BukkitTask task = this.taskScheduler.runTimerAsync(this::dispatch, 0L, this.delayTicks);
        this.currentTask = task.getTaskId();
    }


    /**
     * Selects the next message to dispatch from the configured messages list
     * according to the current {@link AutoMessageConfiguration#mode}.
     * <p>
     * If the mode is RANDOM, a random message is selected.
     * If the mode is SEQUENTIAL, messages are dispatched in order, cycling through the list.
     * </p>
     *
     * @return the selected {@link Notice} message to dispatch
     * @throws IllegalStateException if no messages are available for selection
     */
    private @NotNull Notice selectMessage() {
        List<Notice> messages = this.configuration.messages;

        return switch (this.configuration.mode) {
            case RANDOM -> CollectionUtil.getRandom(messages).orElseThrow(
                    () -> new IllegalStateException("No messages available for random selection")
            );
            case SEQUENTIAL -> {
                int index = this.position.getAndIncrement() % messages.size();
                yield CollectionUtil.select(messages, index).orElseThrow(
                        () -> new IllegalStateException("No message at position: " + index)
                );
            }
        };
    }

    /**
     * Returns the current delay between automatic message dispatches as a {@link Duration}.
     * <p>
     * This value reflects the delay currently in use by the scheduled task,
     * and may differ from the original configuration if it has been changed dynamically
     * via {@link #changeDelay(long)}.
     * </p>
     *
     * @return the delay between automatic messages, represented as a {@link Duration}
     */
    @NotNull
    public Duration getDelay() {
        return DurationUtil.fromTicks(this.delayTicks);
    }
}
