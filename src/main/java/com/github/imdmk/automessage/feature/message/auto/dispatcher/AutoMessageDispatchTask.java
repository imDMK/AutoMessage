package com.github.imdmk.automessage.feature.message.auto.dispatcher;

import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Scheduled task that periodically dispatches automatic messages.
 */
final class AutoMessageDispatchTask extends BukkitRunnable {

    private final Server server;
    private final AutoMessageDispatcher dispatcher;

    public AutoMessageDispatchTask(@NotNull Server server, @NotNull AutoMessageDispatcher dispatcher) {
        this.server = Objects.requireNonNull(server, "server cannot be null");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher cannot be null");
    }

    @Override
    public void run() {
        if (this.dispatcher.isEnabled()) {
            this.server.getOnlinePlayers().forEach(this.dispatcher::dispatch);
        }
    }
}
