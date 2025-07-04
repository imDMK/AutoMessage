package com.github.imdmk.automessage.feature.message.auto.dispatcher;

import com.github.imdmk.automessage.configuration.ConfigurationManager;
import com.github.imdmk.automessage.feature.message.MessageService;
import com.github.imdmk.automessage.feature.message.auto.AutoMessageConfig;
import com.github.imdmk.automessage.feature.message.auto.AutoMessageNotice;
import com.github.imdmk.automessage.feature.message.auto.eligibility.AutoMessageEligibilityEvaluator;
import com.github.imdmk.automessage.feature.message.auto.selector.AutoMessageSelector;
import com.github.imdmk.automessage.feature.message.auto.selector.AutoMessageSelectorFactory;
import com.github.imdmk.automessage.scheduler.TaskScheduler;
import com.github.imdmk.automessage.util.DurationUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;

/**
 * Dispatches automatic messages to online players based on configured strategy.
 */
public final class AutoMessageDispatcher {

    /**
     * The initial delay in ticks before the first auto message dispatch.
     */
    private static final long INITIAL_DELAY_TICKS = 0L;

    /**
     * Indicates that task is not currently scheduled.
     */
    private static final int TASK_NOT_RUN_ID = 0;

    private final Server server;
    private final ConfigurationManager configurationManager;
    private final AutoMessageConfig configuration;
    private final MessageService messageService;
    private final TaskScheduler taskScheduler;

    private final AutoMessageSelector selector;

    private volatile long delayTicks;
    private volatile int currentTask;

    public AutoMessageDispatcher(
            @NotNull Server server,
            @NotNull ConfigurationManager configurationManager,
            @NotNull AutoMessageConfig configuration,
            @NotNull MessageService messageService,
            @NotNull TaskScheduler taskScheduler,
            @NotNull AutoMessageEligibilityEvaluator eligibilityEvaluator
    ) {
        this.server = Objects.requireNonNull(server, "server cannot be null");
        this.configurationManager = Objects.requireNonNull(configurationManager, "configurationManager cannot be null");
        this.configuration = Objects.requireNonNull(configuration, "configuration cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.taskScheduler = Objects.requireNonNull(taskScheduler, "taskScheduler cannot be null");

        this.selector = AutoMessageSelectorFactory.create(this.configuration.mode, eligibilityEvaluator);
        this.delayTicks = DurationUtil.toTicks(Objects.requireNonNull(this.configuration.delay, "delay cannot be null"));
    }

    /**
     * Dispatches an automatic message to the specified player if an eligible message is available.
     *
     * @param player the player to receive the message, must not be {@code null}
     */
    public void dispatch(@NotNull Player player) {
        this.selector.selectFor(player, this.configuration.messages)
                .map(AutoMessageNotice::getNotice)
                .ifPresent(notice -> this.messageService.create()
                        .viewer(player)
                        .notice(notice)
                        .sendAsync());
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

        this.configuration.setDelay(DurationUtil.fromTicks(newDelayTicks));
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
        if (this.isTaskScheduled()) {
            this.taskScheduler.cancelTask(this.currentTask);
            this.selector.reset();
        }

        BukkitTask task = this.taskScheduler.runTimerAsync(
                new AutoMessageDispatchTask(this.server, this), INITIAL_DELAY_TICKS, this.delayTicks
        );
        this.currentTask = task.getTaskId();
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

    /**
     * Checks whether an auto message dispatch task is currently scheduled.
     * <p>
     * A task is considered scheduled if its task ID is greater than {@link #TASK_NOT_RUN_ID},
     * which indicates that a valid Bukkit task has been assigned.
     * </p>
     *
     * @return {@code true} if a task is currently scheduled; {@code false} otherwise
     */
    public boolean isTaskScheduled() {
        return this.currentTask > TASK_NOT_RUN_ID;
    }

}
