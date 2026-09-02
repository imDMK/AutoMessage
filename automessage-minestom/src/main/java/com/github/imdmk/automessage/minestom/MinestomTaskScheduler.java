package com.github.imdmk.automessage.minestom;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.platform.scheduler.TaskHandle;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MinestomTaskScheduler implements TaskScheduler {

    private final Scheduler scheduler;

    // Held because Minestom's scheduler cancels per task, not per owner: nothing here belongs to
    // a plugin it could be asked about, so the plugin has to remember what it started.
    private final Set<Task> tasks = ConcurrentHashMap.newKeySet();

    private volatile ExecutorService asyncExecutor;

    public MinestomTaskScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public TaskHandle runAsync(Runnable runnable) {
        asyncExecutor().execute(runnable);

        // Deliberately not cancellable. Handing back a handle over a Future would promise an
        // interruption this plugin never asks for, and nothing calls cancel on this one.
        return TaskHandle.done();
    }

    @Override
    public TaskHandle runLaterSync(Runnable runnable, Duration delay) {
        return submit(scheduler.buildTask(runnable).delay(delay));
    }

    @Override
    public TaskHandle runTimerSync(PluginTask task) {
        return submit(scheduler.buildTask(task)
                .delay(task.delay())
                .repeat(task.period()));
    }

    @Override
    public void shutdown() {
        tasks.forEach(Task::cancel);
        tasks.clear();

        final ExecutorService executor = asyncExecutor;
        if (executor != null) {
            executor.shutdown();
        }
    }

    private TaskHandle submit(Task.Builder builder) {
        // TICK_START, so a broadcast is written before the tick's own work rather than after it,
        // which is where Bukkit's scheduled tasks run too.
        final Task task = builder.executionType(ExecutionType.TICK_START).schedule();
        tasks.add(task);

        return () -> {
            task.cancel();
            tasks.remove(task);
        };
    }

    private ExecutorService asyncExecutor() {
        ExecutorService executor = asyncExecutor;
        if (executor != null) {
            return executor;
        }

        synchronized (this) {
            if (asyncExecutor == null) {
                this.asyncExecutor = Executors.newSingleThreadExecutor(
                        runnable -> Thread.ofPlatform().name("AutoMessage Async").unstarted(runnable)
                );
            }

            return asyncExecutor;
        }
    }
}
