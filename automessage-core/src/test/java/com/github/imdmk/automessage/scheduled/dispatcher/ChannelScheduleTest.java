package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.platform.scheduler.TaskHandle;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.ScheduledMessageSender;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceContext;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import com.github.imdmk.automessage.config.ConfigReloadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

// /automessage next has to say how long is left, and /automessage send has to be able to push one
// out now. Neither answer exists anywhere but here, because a scheduler will not say what is left
// on a timer it owns.
class ChannelScheduleTest {

    private static final Duration PERIOD = Duration.ofMinutes(5);
    private static final Duration INITIAL = Duration.ofMinutes(1);

    private final AtomicLong clock = new AtomicLong(Duration.ofDays(2).toNanos());
    private final List<PluginTask> timers = new ArrayList<>();
    private final List<Viewer> online = new ArrayList<>();
    private final ScheduledMessagesConfig messages = new ScheduledMessagesConfig();
    private final MessageDispatcherConfig config = new MessageDispatcherConfig();

    private void advance(Duration duration) {
        clock.addAndGet(duration.toNanos());
    }

    private static AnnouncementChannel channel(String name, boolean enabled) {
        return new AnnouncementChannel(name, enabled, INITIAL, PERIOD, MessageSelectorType.SEQUENTIAL);
    }

    private MessageDispatcherService service() {
        final ViewerRegistry viewers = new ViewerRegistry() {
            @Override
            public Collection<Viewer> online() {
                return List.copyOf(online);
            }

            @Override
            public int onlineCount() {
                return online.size();
            }

            @Override
            public int maxPlayers() {
                return 20;
            }
        };

        final TaskScheduler scheduler = new TaskScheduler() {
            @Override
            public TaskHandle runAsync(Runnable runnable) {
                return TaskHandle.done();
            }

            @Override
            public TaskHandle runLaterSync(Runnable runnable, Duration delay) {
                return TaskHandle.done();
            }

            @Override
            public TaskHandle runTimerSync(PluginTask task) {
                timers.add(task);
                return () -> timers.remove(task);
            }

            @Override
            public void shutdown() {
            }
        };

        final ScheduledMessageRepository repository =
                ScheduledMessageRepository.config(messages, new ConfigReloadService(null));

        final MessageDispatcherService service = new MessageDispatcherService(
                mock(PluginLogger.class), viewers, scheduler, config, repository,
                selector -> new MessageDispatcher(
                        mock(ScheduledMessageSender.class), selector, AudienceFilter.ruleFilter(),
                        AudienceContext.of(viewers, PlaytimeSource.unavailable()), DispatchObserver.none()
                ),
                clock::get
        );
        service.start();
        return service;
    }

    private ChannelPreview only(MessageDispatcherService service) {
        return service.upcoming().getFirst();
    }

    @Test
    @DisplayName("counts down from the initial delay before the first announcement")
    void countsDownFromTheInitialDelay() {
        messages.messages = List.of(new ScheduledMessage("alpha", List.of()));
        config.channels = List.of(channel("default", true));
        final MessageDispatcherService service = service();

        assertThat(only(service).due()).isEqualTo(INITIAL);

        advance(Duration.ofSeconds(50));
        assertThat(only(service).due()).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("starts the full period again once the channel has fired")
    void restartsThePeriodAfterFiring() {
        messages.messages = List.of(new ScheduledMessage("alpha", List.of()));
        config.channels = List.of(channel("default", true));
        final MessageDispatcherService service = service();

        advance(INITIAL);
        timers.getFirst().run();

        assertThat(only(service).due()).isEqualTo(PERIOD);
    }

    @Test
    @DisplayName("counts down even when nobody heard the last one")
    void countsDownWithNobodyOnline() {
        messages.messages = List.of(new ScheduledMessage("alpha", List.of()));
        config.channels = List.of(channel("default", true));
        final MessageDispatcherService service = service();

        // The timer fired and sent nothing, but the channel is still due again in a period.
        advance(INITIAL);
        timers.getFirst().run();
        advance(Duration.ofMinutes(2));

        assertThat(only(service).due()).isEqualTo(Duration.ofMinutes(3));
    }

    @Test
    @DisplayName("sends on demand and starts the interval again from that moment")
    void sendsOnDemandAndRestartsTheInterval() {
        messages.messages = List.of(new ScheduledMessage("alpha", List.of()));
        config.channels = List.of(channel("default", true));
        online.add(mock(Viewer.class));
        final MessageDispatcherService service = service();
        final PluginTask before = timers.getFirst();

        advance(Duration.ofSeconds(55));
        final ForcedSend result = service.forceNext(config.channels().getFirst());

        assertThat(result.kind()).isEqualTo(ForcedSend.Kind.SENT);
        assertThat(result.message()).map(ScheduledMessage::name).contains("alpha");
        // Five seconds were left; pushing one out by hand must not be followed by that one.
        assertThat(only(service).due()).isEqualTo(PERIOD);
        // Replaced rather than joined: one timer, and not the one that was ticking before.
        assertThat(timers).hasSize(1).doesNotContain(before);
    }

    @Test
    @DisplayName("nobody online means nothing sent and the schedule left alone")
    void nobodyOnlineLeavesTheScheduleAlone() {
        messages.messages = List.of(new ScheduledMessage("alpha", List.of()));
        config.channels = List.of(channel("default", true));
        final MessageDispatcherService service = service();

        advance(Duration.ofSeconds(55));
        final ForcedSend result = service.forceNext(config.channels().getFirst());

        assertThat(result.kind()).isEqualTo(ForcedSend.Kind.NOBODY_ONLINE);
        assertThat(only(service).due()).isEqualTo(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("a channel with nothing assigned says so and keeps its place")
    void emptyChannelKeepsItsPlace() {
        config.channels = List.of(channel("ads", true));
        online.add(mock(Viewer.class));
        final MessageDispatcherService service = service();

        final PluginTask before = timers.getFirst();
        advance(Duration.ofSeconds(55));
        final ForcedSend result = service.forceNext(config.channels().getFirst());

        assertThat(result.kind()).isEqualTo(ForcedSend.Kind.NO_MESSAGES);
        // Nothing went out, so the timer that was already ticking is the same one, untouched.
        assertThat(timers).containsExactly(before);
    }

    @Test
    @DisplayName("a channel switched off in the file cannot be pushed")
    void disabledChannelRefuses() {
        messages.messages = List.of(new ScheduledMessage("alpha", List.of()));
        config.channels = List.of(channel("default", false));
        online.add(mock(Viewer.class));
        final MessageDispatcherService service = service();

        final ForcedSend result = service.forceNext(config.channels().getFirst());

        assertThat(result.kind()).isEqualTo(ForcedSend.Kind.DISABLED);
        assertThat(only(service).due()).isNull();
    }
}
