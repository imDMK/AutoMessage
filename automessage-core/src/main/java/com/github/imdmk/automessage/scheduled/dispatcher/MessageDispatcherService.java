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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MessageDispatcherService implements ConfigReloadListener {

    private final PluginLogger logger;
    private final ViewerRegistry viewers;
    private final TaskScheduler taskScheduler;
    private final MessageDispatcherConfig dispatcherConfig;
    private final ScheduledMessageRepository repository;
    private final ScheduledMessageDispatcherFactory dispatcherFactory;

    private final List<TaskHandle> tasks = new ArrayList<>();

    // Selectors are stateful - SEQUENTIAL and SHUFFLE remember where they stopped - so they
    // outlive the tasks and a reload does not send every channel back to the top of its rotation.
    private final Map<String, MessageSelectorProvider> selectorsByChannel = new HashMap<>();

    public MessageDispatcherService(
            PluginLogger logger,
            ViewerRegistry viewers,
            TaskScheduler taskScheduler,
            MessageDispatcherConfig dispatcherConfig,
            ScheduledMessageRepository repository,
            ScheduledMessageDispatcherFactory dispatcherFactory
    ) {
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

        final MessageSelectorProvider selectors = selectorsByChannel.computeIfAbsent(
                AnnouncementChannel.normalize(channel.name()),
                key -> new MessageSelectorProvider(() -> currentSelectorOf(key))
        );

        final MessageDispatcherTask task = new MessageDispatcherTask(
                viewers,
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

    private com.github.imdmk.automessage.scheduled.selector.MessageSelectorType currentSelectorOf(String channelName) {
        for (final AnnouncementChannel channel : dispatcherConfig.channels()) {
            if (channel.matches(channelName)) {
                return channel.selector();
            }
        }

        return com.github.imdmk.automessage.scheduled.selector.MessageSelectorType.SEQUENTIAL;
    }

    public synchronized void stop() {
        for (final TaskHandle task : tasks) {
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
