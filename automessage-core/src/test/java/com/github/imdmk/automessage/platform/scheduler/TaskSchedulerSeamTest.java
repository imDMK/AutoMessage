package com.github.imdmk.automessage.platform.scheduler;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceContext;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.dispatcher.DispatchObserver;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcher;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherService;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TaskSchedulerSeamTest {

    private static final class RecordingScheduler implements TaskScheduler {

        private final List<PluginTask> repeating = new ArrayList<>();
        private final List<Duration> delays = new ArrayList<>();
        private final List<Runnable> asyncWork = new ArrayList<>();

        private int cancelled;
        private boolean shutDown;

        @Override
        public TaskHandle runAsync(Runnable runnable) {
            asyncWork.add(runnable);
            return () -> cancelled++;
        }

        @Override
        public TaskHandle runLaterSync(Runnable runnable, Duration delay) {
            delays.add(delay);
            return () -> cancelled++;
        }

        @Override
        public TaskHandle runTimerSync(PluginTask task) {
            repeating.add(task);
            return () -> cancelled++;
        }

        @Override
        public void shutdown() {
            shutDown = true;
        }
    }

    private static final class EmptyRepository implements ScheduledMessageRepository {

        @Override
        public List<ScheduledMessage> findAll() {
            return List.of();
        }

        @Override
        public List<ScheduledMessage> findScheduled() {
            return List.of();
        }

        @Override
        public List<ScheduledMessage> findByChannel(AnnouncementChannel channel) {
            return List.of();
        }

        @Override
        public List<ScheduledMessage> findByTrigger(MessageTrigger.Type type) {
            return List.of();
        }

        @Override
        public Optional<ScheduledMessage> findByName(String name) {
            return Optional.empty();
        }

        @Override
        public List<String> names() {
            return List.of();
        }
    }

    private static final ViewerRegistry EMPTY_REGISTRY = new ViewerRegistry() {

        @Override
        public java.util.Collection<Viewer> online() {
            return List.of();
        }

        @Override
        public int onlineCount() {
            return 0;
        }

        @Override
        public int maxPlayers() {
            return 0;
        }
    };

    private static final AudienceContext CONTEXT =
            AudienceContext.of(EMPTY_REGISTRY, PlaytimeSource.unavailable());

    private final RecordingScheduler scheduler = new RecordingScheduler();

    private MessageDispatcherService serviceWith(AnnouncementChannel... channels) {
        MessageDispatcherConfig config = new MessageDispatcherConfig();
        config.channels = List.of(channels);

        return new MessageDispatcherService(
                mock(PluginLogger.class),
                EMPTY_REGISTRY,
                scheduler,
                config,
                new EmptyRepository(),
                selector -> new MessageDispatcher(
                        null, selector, AudienceFilter.ruleFilter(), CONTEXT, DispatchObserver.none())
        );
    }

    private static AnnouncementChannel channel(String name, boolean enabled) {
        return new AnnouncementChannel(
                name, enabled, Duration.ofMinutes(1), Duration.ofMinutes(5), MessageSelectorType.SHUFFLE
        );
    }

    @Test
    @DisplayName("schedules one repeating task per enabled channel, through the abstraction alone")
    void schedulesThroughTheSeam() {
        serviceWith(channel("default", true), channel("ads", true)).start();

        assertThat(scheduler.repeating).hasSize(2);
        assertThat(scheduler.repeating.getFirst().period()).isEqualTo(Duration.ofMinutes(5));
        assertThat(scheduler.repeating.getFirst().delay()).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    @DisplayName("a disabled channel is not scheduled at all")
    void skipsDisabledChannels() {
        serviceWith(channel("default", true), channel("ads", false)).start();

        assertThat(scheduler.repeating).hasSize(1);
    }

    @Test
    @DisplayName("stopping cancels every handle it was given")
    void cancelsThroughTheHandle() {
        MessageDispatcherService service = serviceWith(channel("default", true), channel("ads", true));

        service.start();
        service.stop();

        assertThat(scheduler.cancelled).isEqualTo(2);
    }

    @Test
    @DisplayName("a reload reschedules rather than leaving the old tasks running")
    void restartCancelsBeforeScheduling() {
        MessageDispatcherService service = serviceWith(channel("default", true));

        service.start();
        service.onConfigReload();

        assertThat(scheduler.cancelled).isEqualTo(1);
        assertThat(scheduler.repeating).hasSize(2);
    }

    @Test
    @DisplayName("a handle for finished work can be cancelled without effect")
    void doneHandleIsSafe() {
        TaskHandle.done().cancel();
    }
}
