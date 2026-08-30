package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.config.ConfigReloadListener;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.time.DurationFormatter;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorProvider;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns one repeating task per announcement channel.
 *
 * <p>
 * Bukkit captures the delay and the period when a task is scheduled, so a changed interval cannot
 * take effect while the task is running. Reloading the configuration therefore cancels every task
 * and schedules a fresh set with the timing that was just read.
 * </p>
 *
 * <p>
 * Each channel keeps its own selector across reloads, so re-reading the configuration does not
 * send every channel back to the top of its rotation.
 * </p>
 *
 * <p>
 * The ticks run on the main thread: they read the online player collection and evaluate the
 * audience rules, and neither is safe to touch asynchronously. Only the delivery of each notice is
 * handed off, so a tick costs one pass over the online players and nothing else.
 * </p>
 */
public final class MessageDispatcherService implements ConfigReloadListener {

    private final PluginLogger logger;
    private final Server server;
    private final TaskScheduler taskScheduler;
    private final MessageDispatcherConfig dispatcherConfig;
    private final ScheduledMessageRepository repository;
    private final ScheduledMessageDispatcherFactory dispatcherFactory;

    private final List<BukkitTask> tasks = new ArrayList<>();

    /**
     * Selectors are stateful — SEQUENTIAL and SHUFFLE remember where they stopped — so they are
     * kept per channel across restarts of the tasks rather than rebuilt with them.
     */
    private final Map<String, MessageSelectorProvider> selectorsByChannel = new HashMap<>();

    public MessageDispatcherService(
            PluginLogger logger,
            Server server,
            TaskScheduler taskScheduler,
            MessageDispatcherConfig dispatcherConfig,
            ScheduledMessageRepository repository,
            ScheduledMessageDispatcherFactory dispatcherFactory
    ) {
        this.logger = logger;
        this.server = server;
        this.taskScheduler = taskScheduler;
        this.dispatcherConfig = dispatcherConfig;
        this.repository = repository;
        this.dispatcherFactory = dispatcherFactory;
    }

    public synchronized void start() {
        if (!tasks.isEmpty()) {
            return;
        }

        final List<AnnouncementChannel> channels = dispatcherConfig.channels();
        forgetSelectorsOfRemovedChannels(channels);

        for (final AnnouncementChannel channel : channels) {
            if (!channel.enabled()) {
                logger.info("Channel '%s' is disabled and was not scheduled.", channel.name());
                continue;
            }

            schedule(channel);
        }

        warnAboutOrphanedMessages();
    }

    /**
     * Drops the rotation state of channels that no longer exist.
     *
     * <p>
     * Selectors are deliberately kept across reloads so a channel does not restart its rotation
     * every time the configuration is read. A channel that has been renamed or deleted, however,
     * is never asked for again - and a SHUFFLE selector holds a deck of the messages it was
     * dealing, so leaving the entry behind pins those objects for the life of the server.
     * </p>
     */
    private void forgetSelectorsOfRemovedChannels(List<AnnouncementChannel> channels) {
        selectorsByChannel.keySet().removeIf(
                name -> channels.stream().noneMatch(channel -> channel.matches(name))
        );
    }

    /**
     * A message pointing at a channel nobody declared is never sent, and nothing about the running
     * server says why. A typo in one line of YAML is otherwise invisible until somebody notices an
     * announcement has been missing for a week.
     */
    private void warnAboutOrphanedMessages() {
        final List<AnnouncementChannel> channels = dispatcherConfig.channels();

        for (final var message : repository.findAll()) {
            final boolean known = channels.stream().anyMatch(channel -> channel.matches(message.channel()));

            if (!known) {
                logger.warn(
                        "Message '%s' names channel '%s', which is not configured in "
                                + "messagesDispatcher.yml - it will never be sent.",
                        message.name(),
                        message.channel()
                );
            }
        }
    }

    private void schedule(AnnouncementChannel channel) {
        final DispatchTiming timing = DispatchTiming.from(channel, logger);

        final MessageSelectorProvider selectors = selectorsByChannel.computeIfAbsent(
                AnnouncementChannel.normalize(channel.name()),
                key -> new MessageSelectorProvider(() -> currentSelectorOf(key))
        );

        final MessageDispatcherTask task = new MessageDispatcherTask(
                server,
                dispatcherConfig::isEnabled,
                channel,
                repository,
                dispatcherFactory.create(selectors),
                timing
        );

        tasks.add(taskScheduler.runTimerSync(task));

        logger.info(
                "Channel '%s': a message every %s, first one in %s, rotation %s.",
                channel.name(),
                DurationFormatter.format(timing.period()),
                DurationFormatter.format(timing.initialDelay()),
                channel.selector()
        );
    }

    /**
     * Read through the configuration rather than captured, so a reload that changes a channel's
     * rotation is picked up without discarding the position that channel had reached.
     */
    private com.github.imdmk.automessage.scheduled.selector.MessageSelectorType currentSelectorOf(String channelName) {
        for (final AnnouncementChannel channel : dispatcherConfig.channels()) {
            if (channel.matches(channelName)) {
                return channel.selector();
            }
        }

        return com.github.imdmk.automessage.scheduled.selector.MessageSelectorType.SEQUENTIAL;
    }

    public synchronized void stop() {
        for (final BukkitTask task : tasks) {
            task.cancel();
        }

        tasks.clear();
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
