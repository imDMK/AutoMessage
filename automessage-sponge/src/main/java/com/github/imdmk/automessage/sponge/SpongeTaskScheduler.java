package com.github.imdmk.automessage.sponge;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.platform.scheduler.TaskHandle;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Game;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class SpongeTaskScheduler implements TaskScheduler {

    private final PluginContainer container;
    private final Game game;

    // Sponge's async scheduler belongs to the game and exists from plugin construction; the
    // synchronous one belongs to the server engine, which does not exist yet at that point. The
    // plugin has to be built that early because RegisterCommandEvent "does not guarantee that any
    // specific engine is running", so submissions made before the engine arrives are held here
    // and replayed in engineStarted() rather than quietly running off the server thread.
    private final Queue<DeferredTask> deferred = new ConcurrentLinkedQueue<>();

    public SpongeTaskScheduler(PluginContainer container, Game game) {
        this.container = container;
        this.game = game;
    }

    @Override
    public TaskHandle runAsync(Runnable runnable) {
        // The game's own pool, available from plugin construction onwards - nothing to defer.
        return submit(game.asyncScheduler(), runnable, Duration.ZERO, null);
    }

    @Override
    public TaskHandle runLaterSync(Runnable runnable, Duration delay) {
        return submitSync(runnable, delay, null);
    }

    @Override
    public TaskHandle runTimerSync(PluginTask task) {
        return submitSync(task, task.delay(), task.period());
    }

    public void engineStarted() {
        final Scheduler scheduler = game.server().scheduler();

        DeferredTask task;
        while ((task = deferred.poll()) != null) {
            task.start(scheduler);
        }
    }

    @Override
    public void shutdown() {
        deferred.forEach(DeferredTask::cancel);
        deferred.clear();

        game.asyncScheduler().tasks(container).forEach(ScheduledTask::cancel);

        if (game.isServerAvailable()) {
            game.server().scheduler().tasks(container).forEach(ScheduledTask::cancel);
        }
    }

    private TaskHandle submitSync(Runnable runnable, Duration delay, @Nullable Duration period) {
        if (game.isServerAvailable()) {
            return submit(game.server().scheduler(), runnable, delay, period);
        }

        final DeferredTask task = new DeferredTask(runnable, delay, period);
        deferred.add(task);

        return task;
    }

    private TaskHandle submit(Scheduler scheduler, Runnable runnable, Duration delay, @Nullable Duration period) {
        final Task.Builder builder = Task.builder()
                .plugin(container)
                .execute(runnable)
                .delay(delay);

        if (period != null) {
            builder.interval(period);
        }

        final ScheduledTask task = scheduler.submit(builder.build());
        return task::cancel;
    }

    private final class DeferredTask implements TaskHandle {

        private final Runnable runnable;
        private final Duration delay;
        private final @Nullable Duration period;

        private volatile boolean cancelled;
        private volatile @Nullable TaskHandle started;

        private DeferredTask(Runnable runnable, Duration delay, @Nullable Duration period) {
            this.runnable = runnable;
            this.delay = delay;
            this.period = period;
        }

        // Cancelling before the engine starts has to stick: the caller cannot tell its task was
        // never really scheduled, and one coming back to life on startup is a broadcast nobody
        // asked for.
        private void start(Scheduler scheduler) {
            if (cancelled) {
                return;
            }

            this.started = submit(scheduler, runnable, delay, period);

            // Cancelled between the check above and the submission: honour it now.
            if (cancelled) {
                started.cancel();
            }
        }

        @Override
        public void cancel() {
            this.cancelled = true;

            final TaskHandle handle = started;
            if (handle != null) {
                handle.cancel();
            }
        }
    }
}
