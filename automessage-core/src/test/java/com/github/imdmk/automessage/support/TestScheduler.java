package com.github.imdmk.automessage.support;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.platform.scheduler.TaskHandle;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// A clock the test winds by hand. Timing is not what these tests are about - whether the work
// scheduled is the right work is.
public final class TestScheduler implements TaskScheduler {

    private final List<Entry> timers = new ArrayList<>();
    private final List<Entry> delayed = new ArrayList<>();

    private boolean shutdown;

    public int runningTimers() {
        return (int) timers.stream().filter(entry -> !entry.cancelled).count();
    }

    public boolean isShutdown() {
        return shutdown;
    }

    // One pass of every repeating task that is still alive.
    public void tick() {
        List.copyOf(timers).stream()
                .filter(entry -> !entry.cancelled)
                .forEach(entry -> entry.action.run());
    }

    // Everything scheduled with a delay, as though the delay had elapsed.
    public void runDelayed() {
        final List<Entry> due = List.copyOf(delayed);
        delayed.clear();

        due.stream().filter(entry -> !entry.cancelled).forEach(entry -> entry.action.run());
    }

    @Override
    public TaskHandle runAsync(Runnable runnable) {
        runnable.run();
        return TaskHandle.done();
    }

    @Override
    public TaskHandle runLaterSync(Runnable runnable, Duration delay) {
        final Entry entry = new Entry(runnable);
        delayed.add(entry);

        return entry::cancel;
    }

    @Override
    public TaskHandle runTimerSync(PluginTask task) {
        final Entry entry = new Entry(task);
        timers.add(entry);

        return entry::cancel;
    }

    @Override
    public void shutdown() {
        this.shutdown = true;
        timers.forEach(Entry::cancel);
    }

    private static final class Entry {

        private final Runnable action;
        private boolean cancelled;

        private Entry(Runnable action) {
            this.action = action;
        }

        private void cancel() {
            this.cancelled = true;
        }
    }
}
