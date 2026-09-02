package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.config.ConfigReloadListener;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.scheduler.TaskHandle;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.notice.time.DurationFormatter;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorProvider;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.Map;

public final class MessageDispatcherService implements ConfigReloadListener {

    private final PluginLogger logger;
    private final ViewerRegistry viewers;
    private final TaskScheduler taskScheduler;
    private final MessageDispatcherConfig dispatcherConfig;
    private final ScheduledMessageRepository repository;
    private final ScheduledMessageDispatcherFactory dispatcherFactory;

    // Keyed by channel, because two questions need the task itself and not just its handle:
    // how long until it fires, and send it now.
    private final Map<String, Running> running = new LinkedHashMap<>();

    // Selectors are stateful - SEQUENTIAL and SHUFFLE remember where they stopped - so they
    // outlive the tasks and a reload does not send every channel back to the top of its rotation.
    private final Map<String, MessageSelectorProvider> selectorsByChannel = new HashMap<>();

    private final LongSupplier clock;

    public MessageDispatcherService(
            PluginLogger logger,
            ViewerRegistry viewers,
            TaskScheduler taskScheduler,
            MessageDispatcherConfig dispatcherConfig,
            ScheduledMessageRepository repository,
            ScheduledMessageDispatcherFactory dispatcherFactory
    ) {
        this(logger, viewers, taskScheduler, dispatcherConfig, repository, dispatcherFactory, System::nanoTime);
    }

    // The clock is handed in so a test can ask how long is left without waiting for it.
    public MessageDispatcherService(
            PluginLogger logger,
            ViewerRegistry viewers,
            TaskScheduler taskScheduler,
            MessageDispatcherConfig dispatcherConfig,
            ScheduledMessageRepository repository,
            ScheduledMessageDispatcherFactory dispatcherFactory,
            LongSupplier clock
    ) {
        this.clock = clock;
        this.logger = logger;
        this.viewers = viewers;
        this.taskScheduler = taskScheduler;
        this.dispatcherConfig = dispatcherConfig;
        this.repository = repository;
        this.dispatcherFactory = dispatcherFactory;
    }

    // Schedulers capture the delay and period when a task is created, so a changed interval
    // cannot take effect while the task runs - a reload cancels every task and schedules afresh.
    public synchronized void start() {
        if (!running.isEmpty()) {
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

    // A SHUFFLE selector holds a deck of the messages it was dealing, so an entry for a channel
    // that no longer exists pins those objects for the life of the server.
    private void forgetSelectorsOfRemovedChannels(List<AnnouncementChannel> channels) {
        selectorsByChannel.keySet().removeIf(
                name -> channels.stream().noneMatch(channel -> channel.matches(name))
        );
    }

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

        final MessageSelectorProvider selectors = selectorsFor(channel);

        launch(channel, timing, selectors);

        logger.info(
                "Channel '%s': a message every %s, first one in %s, rotation %s.",
                channel.name(),
                DurationFormatter.formatReadable(timing.period()),
                DurationFormatter.formatReadable(timing.initialDelay()),
                channel.selector()
        );
    }

    private com.github.imdmk.automessage.scheduled.selector.MessageSelectorType currentSelectorOf(String channelName) {
        for (final AnnouncementChannel channel : dispatcherConfig.channels()) {
            if (channel.matches(channelName)) {
                return channel.selector();
            }
        }

        return com.github.imdmk.automessage.scheduled.selector.MessageSelectorType.SEQUENTIAL;
    }

    @Nullable
    private Duration untilDue(AnnouncementChannel channel) {
        final Running entry = running.get(AnnouncementChannel.normalize(channel.name()));
        return entry == null ? null : entry.task().untilDue();
    }

    private record Running(MessageDispatcherTask task, TaskHandle handle, DispatchTiming timing) {
    }

    private MessageSelectorProvider selectorsFor(AnnouncementChannel channel) {
        return selectorsByChannel.computeIfAbsent(
                AnnouncementChannel.normalize(channel.name()),
                key -> new MessageSelectorProvider(() -> currentSelectorOf(key))
        );
    }

    /**
     * What each configured channel would announce next, asked without disturbing the rotation.
     */
    public synchronized List<ChannelPreview> upcoming() {
        final List<ChannelPreview> previews = new ArrayList<>();

        for (final AnnouncementChannel channel : dispatcherConfig.channels()) {
            previews.add(preview(channel));
        }

        return List.copyOf(previews);
    }

    private ChannelPreview preview(AnnouncementChannel channel) {
        if (!channel.enabled()) {
            return ChannelPreview.disabled(channel.name());
        }

        final List<com.github.imdmk.automessage.scheduled.ScheduledMessage> messages =
                repository.findByChannel(channel);

        if (messages.isEmpty()) {
            return ChannelPreview.empty(channel.name());
        }

        if (channel.selector() == com.github.imdmk.automessage.scheduled.selector.MessageSelectorType.RANDOM) {
            return ChannelPreview.unpredictable(channel.name(), untilDue(channel));
        }

        // Asked not to advance, so looking is not the same as sending - the deck a SHUFFLE
        // channel is part way through stays exactly where it was.
        return selectorsFor(channel).get()
                .selectNext(messages, false)
                .map(message -> ChannelPreview.next(channel.name(), message.name(), untilDue(channel)))
                .orElseGet(() -> ChannelPreview.empty(channel.name()));
    }

    private void launch(
            AnnouncementChannel channel,
            DispatchTiming timing,
            MessageSelectorProvider selectors
    ) {
        final MessageDispatcherTask task = new MessageDispatcherTask(
                viewers,
                dispatcherConfig::isEnabled,
                channel,
                repository,
                dispatcherFactory.create(selectors),
                timing,
                clock
        );

        running.put(
                AnnouncementChannel.normalize(channel.name()),
                new Running(task, taskScheduler.runTimerSync(task), timing)
        );
    }

    /**
     * Sends a channel's next announcement now and starts its interval again from this moment.
     */
    public synchronized ForcedSend forceNext(AnnouncementChannel channel) {
        final Running current = running.get(AnnouncementChannel.normalize(channel.name()));

        // A channel switched off in the file has no task to send through, which is a different
        // answer from a name that was never configured - the argument rejects those.
        if (current == null) {
            return ForcedSend.disabled();
        }

        final MessageDispatcherTask.Outcome outcome = current.task().send();

        if (outcome.kind() != MessageDispatcherTask.Outcome.Kind.SENT) {
            return ForcedSend.of(outcome, current.task().untilDue());
        }

        // The interval restarts from this moment, so an announcement pushed out by hand is not
        // followed seconds later by the one that was already due. Only when something did go
        // out - a channel nobody heard has no reason to lose its place in the schedule.
        current.handle().cancel();
        final DispatchTiming restarted = new DispatchTiming(current.timing().period(), current.timing().period());
        launch(current.task().channel(), restarted, selectorsFor(current.task().channel()));

        return ForcedSend.of(outcome, restarted.period());
    }

    public synchronized void stop() {
        for (final Running entry : running.values()) {
            entry.handle().cancel();
        }

        running.clear();
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
