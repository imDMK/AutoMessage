package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

/**
 * Counts what has actually been announced, so an administrator can see the rotation working.
 */
public final class DispatchStatistics implements DispatchObserver {

    private final LongSupplier clock;
    private final Map<String, Counter> byMessage = new ConcurrentHashMap<>();
    private final Map<String, Counter> byChannel = new ConcurrentHashMap<>();
    private final AtomicLong total = new AtomicLong();

    public DispatchStatistics() {
        this(System::nanoTime);
    }

    // Nanoseconds rather than a wall clock: the only question asked of it is how long ago
    // something happened, and that answer should survive the server's clock being corrected.
    DispatchStatistics(LongSupplier clock) {
        this.clock = clock;
    }

    @Override
    public void onDispatched(ScheduledMessage message, MessagePlaceholders placeholders) {
        final long now = clock.getAsLong();

        byMessage.computeIfAbsent(message.name(), name -> new Counter()).record(now);
        byChannel.computeIfAbsent(AnnouncementChannel.normalize(message.channel()), name -> new Counter()).record(now);
        total.incrementAndGet();
    }

    public long total() {
        return total.get();
    }

    /**
     * How much a single channel has carried, for a report that also knows when it fires next.
     */
    public Optional<Entry> channel(String channelName) {
        final String key = AnnouncementChannel.normalize(channelName);
        return Optional.ofNullable(byChannel.get(key)).map(counter -> entry(key, counter, clock.getAsLong()));
    }

    public List<Entry> snapshot() {
        final long now = clock.getAsLong();
        final List<Entry> entries = new ArrayList<>();

        byMessage.forEach((name, counter) -> entries.add(entry(name, counter, now)));

        // Loudest first: the question behind this command is usually which message is dominating
        // the rotation, not what the file happens to list first.
        entries.sort(Comparator.comparingLong(Entry::count).reversed().thenComparing(Entry::name));

        return List.copyOf(entries);
    }

    private static Entry entry(String name, Counter counter, long now) {
        return new Entry(name, counter.count.get(), Duration.ofNanos(now - counter.lastNanos));
    }

    public record Entry(String name, long count, Duration since) {
    }

    private static final class Counter {

        private final AtomicLong count = new AtomicLong();
        private volatile long lastNanos;

        private void record(long now) {
            count.incrementAndGet();
            this.lastNanos = now;
        }
    }
}
