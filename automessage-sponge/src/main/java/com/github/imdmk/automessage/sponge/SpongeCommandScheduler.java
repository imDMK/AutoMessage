package com.github.imdmk.automessage.sponge;

import dev.rollczi.litecommands.scheduler.AbstractMainThreadBasedScheduler;
import org.spongepowered.api.Game;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;

import java.time.Duration;

// The scheduler LiteCommands runs commands on, written here for one reason: the upstream Sponge
// one asks Sponge for the main thread in its constructor, and throws when neither a server nor a
// client engine is running.
//
// That matters because the command binding has to exist before RegisterCommandEvent, which Sponge
// fires with no engine at all - so upstream, the binding cannot be built in time to register
// anything. This one asks at the moment a command actually runs, by which point there is always
// an engine, and is otherwise the same thing.
final class SpongeCommandScheduler extends AbstractMainThreadBasedScheduler {

    private final PluginContainer container;
    private final Game game;

    SpongeCommandScheduler(PluginContainer container, Game game) {
        this.container = container;
        this.game = game;
    }

    @Override
    protected void runSynchronous(Runnable runnable, Duration delay) {
        mainScheduler().submit(task(runnable, delay));
    }

    @Override
    protected void runAsynchronous(Runnable runnable, Duration delay) {
        game.asyncScheduler().submit(task(runnable, delay));
    }

    @Override
    public void shutdown() {
        game.asyncScheduler().tasks(container).forEach(ScheduledTask::cancel);

        if (game.isServerAvailable()) {
            game.server().scheduler().tasks(container).forEach(ScheduledTask::cancel);
        }
    }

    // Nothing can have run a command before an engine exists, so the last branch is unreachable
    // in practice - it is there so a lookup can never be the thing that throws.
    private Scheduler mainScheduler() {
        if (game.isServerAvailable()) {
            return game.server().scheduler();
        }

        if (game.isClientAvailable()) {
            return game.client().scheduler();
        }

        return game.asyncScheduler();
    }

    private Task task(Runnable runnable, Duration delay) {
        return Task.builder()
                .plugin(container)
                .execute(runnable)
                .delay(delay)
                .build();
    }
}
