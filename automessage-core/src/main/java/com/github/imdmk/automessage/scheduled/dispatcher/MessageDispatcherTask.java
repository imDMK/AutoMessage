package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

public final class MessageDispatcherTask implements PluginTask {

    private final ViewerRegistry viewers;
    private final BooleanSupplier masterSwitch;
    private final AnnouncementChannel channel;
    private final ScheduledMessageRepository repository;
    private final MessageDispatcher messageDispatcher;
    private final DispatchTiming timing;
    private final LongSupplier clock;

    // When this channel is next due. A scheduler will not say how much is left on a timer it
    // owns, so the task that runs on it keeps count - which is what /automessage next reads.
    private volatile long dueNanos;

    public MessageDispatcherTask(
            ViewerRegistry viewers,
            BooleanSupplier masterSwitch,
            AnnouncementChannel channel,
            ScheduledMessageRepository repository,
            MessageDispatcher messageDispatcher,
            DispatchTiming timing,
            LongSupplier clock
    ) {
        this.viewers = viewers;
        this.masterSwitch = masterSwitch;
        this.channel = channel;
        this.repository = repository;
        this.messageDispatcher = messageDispatcher;
        this.timing = timing;
        this.clock = clock;
        this.dueNanos = clock.getAsLong() + timing.initialDelay().toNanos();
    }

    @Override
    public void run() {
        // Counted from the moment the timer fired, whether or not anything is sent - a channel
        // with nobody online is still due again a period from now.
        this.dueNanos = clock.getAsLong() + timing.period().toNanos();

        // The master switch is read each tick rather than at schedule time, so /automessage
        // disable takes effect without tearing down and rebuilding every channel's task.
        if (!masterSwitch.getAsBoolean()) {
            return;
        }

        send();
    }

    /**
     * Sends this channel's next announcement, whatever the master switch says.
     */
    public Outcome send() {
        final Collection<Viewer> online = viewers.online();
        if (online.isEmpty()) {
            return Outcome.nobodyOnline();
        }

        final List<ScheduledMessage> messages = repository.findByChannel(channel);
        if (messages.isEmpty()) {
            return Outcome.noMessages();
        }

        return messageDispatcher.dispatchNext(messages, DispatchTarget.viewers(online))
                .map(Outcome::sent)
                .orElseGet(Outcome::noMessages);
    }

    public Duration untilDue() {
        final long remaining = dueNanos - clock.getAsLong();
        return remaining <= 0L ? Duration.ZERO : Duration.ofNanos(remaining);
    }

    public AnnouncementChannel channel() {
        return channel;
    }

    @Override
    public Duration delay() {
        return timing.initialDelay();
    }

    @Override
    public Duration period() {
        return timing.period();
    }

    /**
     * What came of an attempt to send, so a command can say why nothing arrived.
     */
    public record Outcome(Kind kind, Optional<ScheduledMessage> message) {

        public enum Kind {
            SENT,
            NOBODY_ONLINE,
            NO_MESSAGES
        }

        static Outcome sent(ScheduledMessage message) {
            return new Outcome(Kind.SENT, Optional.of(message));
        }

        static Outcome nobodyOnline() {
            return new Outcome(Kind.NOBODY_ONLINE, Optional.empty());
        }

        static Outcome noMessages() {
            return new Outcome(Kind.NO_MESSAGES, Optional.empty());
        }
    }
}
