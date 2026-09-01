package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

/**
 * Counts what has actually been announced, so an administrator can see the rotation working.
 */
public final class DispatchStatistics implements DispatchObserver {

    private final LongSupplier clock;
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
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
        counters.computeIfAbsent(message.name(), name -> new Counter()).record(clock.getAsLong());
        total.incrementAndGet();
    }

    public long total() {
        return total.get();
    }

    public List<Entry> snapshot() {
        final long now = clock.getAsLong();
        final List<Entry> entries = new ArrayList<>();

        counters.forEach((name, counter) -> entries.add(
                new Entry(name, counter.count.get(), Duration.ofNanos(now - counter.lastNanos))
        ));

        // Loudest first: the question behind this command is usually which message is dominating
        // the rotation, not what the file happens to list first.
        entries.sort(Comparator.comparingLong(Entry::count).reversed().thenComparing(Entry::message));

        return List.copyOf(entries);
    }

    public record Entry(String message, long count, Duration since) {
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
