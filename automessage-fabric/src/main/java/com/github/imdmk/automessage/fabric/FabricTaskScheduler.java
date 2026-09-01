package com.github.imdmk.automessage.fabric;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.platform.scheduler.TaskHandle;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FabricTaskScheduler implements TaskScheduler {

    private static final long MILLIS_PER_TICK = 50L;

    // Minecraft hands a mod no scheduler; the tick is the only clock there is, so this keeps its
    // own list and walks it from tick(). That is also what makes "sync" mean here what it means
    // everywhere else: work runs on the server thread, where touching the world is safe.
    private final Queue<TickTask> tasks = new ConcurrentLinkedQueue<>();

    private volatile long currentTick;
    private volatile @Nullable ExecutorService asyncExecutor;

    @Override
    public TaskHandle runAsync(Runnable runnable) {
        asyncExecutor().execute(runnable);

        // Deliberately not cancellable: nothing calls cancel on this one, and a handle over a
        // Future would promise an interruption the plugin never asks for.
        return TaskHandle.done();
    }

    @Override
    public TaskHandle runLaterSync(Runnable runnable, Duration delay) {
        return submit(runnable, ticks(delay), 0L);
    }

    @Override
    public TaskHandle runTimerSync(PluginTask task) {
        // A period of zero ticks would run the task every tick forever; a repeating broadcast
        // configured that fast is a mistake, and one tick is the closest honest answer.
        return submit(task, ticks(task.delay()), Math.max(1L, ticks(task.period())));
    }

    public void tick() {
        final long tick = ++currentTick;

        for (final TickTask task : tasks) {
            if (task.cancelled) {
                tasks.remove(task);
                continue;
            }

            if (tick < task.nextRun) {
                continue;
            }

            if (task.period > 0L) {
                task.nextRun = tick + task.period;
            } else {
                tasks.remove(task);
            }

            task.runnable.run();
        }
    }

    @Override
    public void shutdown() {
        tasks.forEach(task -> task.cancelled = true);
        tasks.clear();

        final ExecutorService executor = asyncExecutor;
        if (executor != null) {
            executor.shutdown();
        }
    }

    private TaskHandle submit(Runnable runnable, long delayTicks, long periodTicks) {
        final TickTask task = new TickTask(runnable, currentTick + Math.max(1L, delayTicks), periodTicks);
        tasks.add(task);

        return () -> {
            task.cancelled = true;
            tasks.remove(task);
        };
    }

    private static long ticks(Duration duration) {
        return duration.toMillis() / MILLIS_PER_TICK;
    }

    private ExecutorService asyncExecutor() {
        ExecutorService executor = asyncExecutor;
        if (executor != null) {
            return executor;
        }

        synchronized (this) {
            if (asyncExecutor == null) {
                // Created on first use: the only caller is a configuration reload an
                // administrator may never run.
                this.asyncExecutor = Executors.newSingleThreadExecutor(
                        runnable -> Thread.ofPlatform().name("AutoMessage Async").unstarted(runnable)
                );
            }

            return asyncExecutor;
        }
    }

    private static final class TickTask {

        private final Runnable runnable;
        private final long period;

        private volatile long nextRun;
        private volatile boolean cancelled;

        private TickTask(Runnable runnable, long nextRun, long period) {
            this.runnable = runnable;
            this.nextRun = nextRun;
            this.period = period;
        }
    }
}
