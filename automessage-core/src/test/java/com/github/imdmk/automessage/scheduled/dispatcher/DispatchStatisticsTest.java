package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class DispatchStatisticsTest {

    // Deliberately not zero: a clock starting at zero cannot tell "never recorded" apart from
    // "recorded at the very first instant", and would hide a sentinel bug in the counter.
    private final AtomicLong clock = new AtomicLong(Duration.ofDays(3).toNanos());
    private final DispatchStatistics statistics = new DispatchStatistics(clock::get);

    private void dispatched(String name) {
        statistics.onDispatched(new ScheduledMessage(name, List.of()), null);
    }

    private void advance(Duration duration) {
        clock.addAndGet(duration.toNanos());
    }

    @Test
    @DisplayName("counts each message separately and everything together")
    void countsPerMessageAndInTotal() {
        dispatched("vote");
        dispatched("vote");
        dispatched("shop");

        assertThat(statistics.total()).isEqualTo(3);
        assertThat(statistics.snapshot())
                .extracting(DispatchStatistics.Entry::message, DispatchStatistics.Entry::count)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("vote", 2L),
                        org.assertj.core.groups.Tuple.tuple("shop", 1L)
                );
    }

    @Test
    @DisplayName("says how long ago each message was last sent")
    void reportsHowLongAgo() {
        dispatched("vote");
        advance(Duration.ofMinutes(5));
        dispatched("shop");
        advance(Duration.ofMinutes(1));

        assertThat(statistics.snapshot())
                .extracting(DispatchStatistics.Entry::message, DispatchStatistics.Entry::since)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("vote", Duration.ofMinutes(6)),
                        org.assertj.core.groups.Tuple.tuple("shop", Duration.ofMinutes(1))
                );
    }

    @Test
    @DisplayName("a message sent again is timed from the last time, not the first")
    void repeatsResetTheClock() {
        dispatched("vote");
        advance(Duration.ofMinutes(10));
        dispatched("vote");
        advance(Duration.ofSeconds(30));

        assertThat(statistics.snapshot())
                .singleElement()
                .extracting(DispatchStatistics.Entry::since)
                .isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    @DisplayName("reports nothing before anything has been announced")
    void emptyBeforeAnythingHappens() {
        assertThat(statistics.total()).isZero();
        assertThat(statistics.snapshot()).isEmpty();
    }

    @Test
    @DisplayName("every observer is told, and the cheap one is told first")
    void combinatorTellsEveryObserverInOrder() {
        final List<String> told = new ArrayList<>();
        final DispatchObserver combined = DispatchObserver.of(
                (message, placeholders) -> told.add("first"),
                (message, placeholders) -> told.add("second")
        );

        combined.onDispatched(new ScheduledMessage("vote", List.of()), null);

        assertThat(told).containsExactly("first", "second");
    }
}
