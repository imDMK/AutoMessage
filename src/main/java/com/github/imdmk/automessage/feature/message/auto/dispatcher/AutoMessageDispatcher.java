package com.github.imdmk.automessage.feature.message.auto.dispatcher;

import com.github.imdmk.automessage.configuration.ConfigurationManager;
import com.github.imdmk.automessage.feature.message.MessageService;
import com.github.imdmk.automessage.feature.message.auto.AutoMessageNotice;
import com.github.imdmk.automessage.feature.message.auto.AutoMessageNoticeConfig;
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
 * Handles dispatching of automatic messages to online players
 * based on configured strategy, eligibility, and scheduling.
 * <p>
 * Messages are selected via {@link AutoMessageSelector} and sent using {@link MessageService}.
 */
public final class AutoMessageDispatcher {

    /**
     * Delay in ticks before the first automatic message is dispatched.
     */
    private static final long INITIAL_DELAY_TICKS = 0L;

    /**
     * Indicates that no task is currently scheduled.
     */
    private static final int TASK_NOT_RUN_ID = 0;

    private final Server server;
    private final ConfigurationManager configurationManager;
    private final AutoMessageNoticeConfig configuration;
    private final MessageService messageService;
    private final TaskScheduler taskScheduler;
    private final AutoMessageSelector selector;

    /**
     * Delay between automatic messages, stored in server ticks.
     */
    private volatile long delayTicks;

    /**
     * ID of the currently scheduled Bukkit task; {@link #TASK_NOT_RUN_ID} if no task is scheduled.
     */
    private volatile int currentTask;

    /**
     * Indicates whether automatic message dispatching is enabled.
     */
    private volatile boolean enabled;

    /**
     * Constructs a new dispatcher using the given dependencies and selector factory.
     * Initializes delay based on configuration.
     *
     * @param server                 the Bukkit server instance
     * @param configurationManager  the configuration manager used to persist delay updates
     * @param configuration         the configuration section for auto messages
     * @param messageService        the message service used to send messages
     * @param taskScheduler         the scheduler used to manage dispatch tasks
     * @param eligibilityEvaluator  evaluator to determine message eligibility
     */
    public AutoMessageDispatcher(
            @NotNull Server server,
            @NotNull ConfigurationManager configurationManager,
            @NotNull AutoMessageNoticeConfig configuration,
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
        this.enabled = true;
    }

    /**
     * Full constructor used for restoring dispatcher state (e.g., from persistence).
     *
     * @param server                the Bukkit server instance
     * @param configurationManager the configuration manager
     * @param configuration        the auto message configuration
     * @param messageService       the message service
     * @param taskScheduler        the task scheduler
     * @param selector             the message selector strategy
     * @param delayTicks           the dispatch delay in ticks
     * @param currentTask          the currently scheduled task ID
     * @param enabled              whether dispatching is enabled
     */
    public AutoMessageDispatcher(
            @NotNull Server server,
            @NotNull ConfigurationManager configurationManager,
            @NotNull AutoMessageNoticeConfig configuration,
            @NotNull MessageService messageService,
            @NotNull TaskScheduler taskScheduler,
            @NotNull AutoMessageSelector selector,
            long delayTicks,
            int currentTask,
            boolean enabled
    ) {
        this.server = Objects.requireNonNull(server, "server cannot be null");
        this.configurationManager = Objects.requireNonNull(configurationManager, "configurationManager cannot be null");
        this.configuration = Objects.requireNonNull(configuration, "configuration cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.taskScheduler = Objects.requireNonNull(taskScheduler, "taskScheduler cannot be null");
        this.selector = Objects.requireNonNull(selector, "selector cannot be null");
        this.delayTicks = delayTicks;
        this.currentTask = currentTask;
        this.enabled = enabled;
    }

    /**
     * Dispatches an automatic message to the specified player if any eligible message is found.
     *
     * @param player the player to receive the message (must not be {@code null})
     */
    public void dispatch(@NotNull Player player) {
        this.dispatch(player, this.selector);
    }

    /**
     * Dispatches an automatic message using the given selector.
     *
     * @param player   the player to receive the message
     * @param selector the selector used to choose a message
     */
    public void dispatch(@NotNull Player player, @NotNull AutoMessageSelector selector) {
        selector.selectFor(player, this.configuration.messages)
                .ifPresent(message -> this.dispatch(player, message));
    }

    /**
     * Sends the contents of the specified automatic message to the player.
     * This includes sounds and all notice components.
     *
     * @param player       the player to receive the message
     * @param autoMessage  the message to be dispatched
     */
    public void dispatch(@NotNull Player player, @NotNull AutoMessageNotice autoMessage) {
        autoMessage.getSound().ifPresent(sound -> sound.play(player));
        autoMessage.getNotices().forEach(notice -> this.messageService.sendAsync(player, notice));
    }

    /**
     * Updates the delay between automatic messages and reschedules the dispatch task.
     *
     * @param newDelayTicks new delay in ticks (must be > 0)
     * @throws IllegalArgumentException if {@code newDelayTicks} is less than or equal to zero
     */
    public void changeDelay(long newDelayTicks) {
        if (newDelayTicks <= 0) {
            throw new IllegalArgumentException("Delay must be positive");
        }

        if (this.delayTicks == newDelayTicks) {
            return;
        }

        this.configuration.setDelay(DurationUtil.fromTicks(newDelayTicks));
        this.configurationManager.save(this.configuration);

        this.delayTicks = newDelayTicks;
        this.schedule();
    }

    /**
     * Schedules or reschedules the repeating asynchronous task that dispatches messages
     * at intervals defined by {@link #delayTicks}. Cancels any previously scheduled task.
     * <p>
     * This method is synchronized to prevent race conditions during rescheduling.
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
     * Cancels the currently running auto message task, if any.
     * This method is synchronized to prevent concurrent modification.
     */
    public synchronized void cancel() {
        if (this.isTaskScheduled()) {
            this.taskScheduler.cancelTask(this.currentTask);
            this.selector.reset();
        }
    }

    /**
     * Returns the currently active delay between message dispatches.
     *
     * @return delay between dispatches as a {@link Duration}
     */
    @NotNull
    public Duration getDelay() {
        return DurationUtil.fromTicks(this.delayTicks);
    }

    /**
     * Returns whether a dispatch task is currently scheduled.
     *
     * @return {@code true} if a task is scheduled; {@code false} otherwise
     */
    public boolean isTaskScheduled() {
        return this.currentTask > TASK_NOT_RUN_ID;
    }

    /**
     * Enables or disables automatic dispatching of messages.
     * When disabled, tasks will continue to run, but no messages will be sent.
     *
     * @param enabled {@code true} to enable; {@code false} to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns whether automatic message dispatching is currently enabled.
     *
     * @return {@code true} if enabled; {@code false} otherwise
     */
    public boolean isEnabled() {
        return this.enabled;
    }
}
