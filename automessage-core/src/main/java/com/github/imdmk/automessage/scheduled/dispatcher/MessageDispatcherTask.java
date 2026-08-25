package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collection;

public final class MessageDispatcherTask implements PluginTask {

    private final Server server;
    private final MessageDispatcherConfig dispatcherConfig;
    private final MessageDispatcher messageDispatcher;
    private final DispatchTiming timing;

    public MessageDispatcherTask(
            Server server,
            MessageDispatcherConfig dispatcherConfig,
            MessageDispatcher messageDispatcher,
            DispatchTiming timing
    ) {
        this.server = server;
        this.dispatcherConfig = dispatcherConfig;
        this.messageDispatcher = messageDispatcher;
        this.timing = timing;
    }

    @Override
    public void run() {
        if (!dispatcherConfig.enabled) {
            return;
        }

        final Collection<? extends Player> onlinePlayers = server.getOnlinePlayers();
        if (onlinePlayers.isEmpty()) {
            return;
        }

        final DispatchTarget target = DispatchTarget.players(onlinePlayers);
        messageDispatcher.dispatchNext(target);
    }

    @Override
    public Duration delay() {
        return timing.initialDelay();
    }

    @Override
    public Duration period() {
        return timing.period();
    }
}
