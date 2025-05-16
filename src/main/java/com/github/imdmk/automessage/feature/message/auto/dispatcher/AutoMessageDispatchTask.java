package com.github.imdmk.automessage.feature.message.auto.dispatcher;


import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Scheduled task that periodically dispatches automatic messages.
 */
public final class AutoMessageDispatchTask extends BukkitRunnable {

    private final AutoMessageDispatcher dispatcher;

    public AutoMessageDispatchTask(@NotNull AutoMessageDispatcher dispatcher) {
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher cannot be null");
    }

    @Override
    public void run() {
        this.dispatcher.dispatch();
    }
}
