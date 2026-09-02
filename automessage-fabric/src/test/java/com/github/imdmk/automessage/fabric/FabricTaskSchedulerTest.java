package com.github.imdmk.automessage.fabric;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.platform.scheduler.TaskHandle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class FabricTaskSchedulerTest {

    private static final Duration TICK = Duration.ofMillis(50);

    private FabricTaskScheduler scheduler;
    private AtomicInteger runs;

    @BeforeEach
    void setUp() {
        this.scheduler = new FabricTaskScheduler();
        this.runs = new AtomicInteger();
    }

    private void tick(int times) {
        for (int i = 0; i < times; i++) {
            scheduler.tick();
        }
    }

    private PluginTask repeating(Duration delay, Duration period) {
        return new PluginTask() {

            @Override
            public void run() {
                runs.incrementAndGet();
            }

            @Override
            public Duration delay() {
                return delay;
            }

            @Override
            public Duration period() {
                return period;
            }
        };
    }

    @Test
    @DisplayName("should wait out a delay before running once")
    void shouldWaitOutADelay() {
        scheduler.runLaterSync(runs::incrementAndGet, TICK.multipliedBy(3));

        tick(2);
        assertThat(runs).hasValue(0);

        tick(1);
        assertThat(runs).hasValue(1);

        // One shot: still one, however long the server runs afterwards.
        tick(10);
        assertThat(runs).hasValue(1);
    }

    @Test
    @DisplayName("should repeat on its period, not on every tick")
    void shouldRepeatOnItsPeriod() {
        scheduler.runTimerSync(repeating(TICK.multipliedBy(2), TICK.multipliedBy(5)));

        tick(2);
        assertThat(runs).hasValue(1);

        tick(4);
        assertThat(runs).hasValue(1);

        tick(1);
        assertThat(runs).hasValue(2);
    }

    @Test
    @DisplayName("should never run a task that was cancelled before its first tick")
    void shouldNotRunACancelledTask() {
        final TaskHandle handle = scheduler.runLaterSync(runs::incrementAndGet, TICK.multipliedBy(3));
        handle.cancel();

        tick(10);
        assertThat(runs).hasValue(0);
    }

    @Test
    @DisplayName("should stop a repeating task where it was cancelled")
    void shouldStopARepeatingTask() {
        final TaskHandle handle = scheduler.runTimerSync(repeating(Duration.ZERO, TICK));

        tick(3);
        assertThat(runs).hasValue(3);

        handle.cancel();

        tick(5);
        assertThat(runs).hasValue(3);
    }

    @Test
    @DisplayName("should treat a sub-tick period as one tick rather than as no period at all")
    void shouldFloorASubTickPeriodToOneTick() {
        scheduler.runTimerSync(repeating(Duration.ZERO, Duration.ofMillis(1)));

        tick(4);
        assertThat(runs).hasValue(4);
    }

    @Test
    @DisplayName("should stop everything it started when the server does")
    void shouldStopEverythingOnShutdown() {
        scheduler.runTimerSync(repeating(Duration.ZERO, TICK));
        scheduler.runLaterSync(runs::incrementAndGet, TICK.multipliedBy(2));

        scheduler.shutdown();

        tick(10);
        assertThat(runs).hasValue(0);
    }
}
