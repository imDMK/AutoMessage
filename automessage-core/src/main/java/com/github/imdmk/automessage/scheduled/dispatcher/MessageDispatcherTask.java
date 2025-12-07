package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.shared.validate.Validator;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;

public final class MessageDispatcherTask implements PluginTask {

    private final Server server;
    private final MessagesDispatcherConfig dispatcherConfig;
    private final MessageDispatcher messageDispatcher;

    public MessageDispatcherTask(
            @NotNull Server server,
            @NotNull MessagesDispatcherConfig dispatcherConfig,
            @NotNull MessageDispatcher messageDispatcher
    ) {
        this.server = Validator.notNull(server, "server");
        this.dispatcherConfig = Validator.notNull(dispatcherConfig, "dispatcherConfig");
        this.messageDispatcher = Validator.notNull(messageDispatcher, "messageDispatcher");
    }

    @Override
    public void run() {
        if (!dispatcherConfig.enabled) {
            return;
        }

        Collection<? extends Player> onlinePlayers = server.getOnlinePlayers();
        if (onlinePlayers.isEmpty()) {
            return;
        }

        DispatchTarget target = DispatchTarget.players(onlinePlayers);
        messageDispatcher.dispatchNext(target);
    }

    @Override
    public Duration delay() {
        return dispatcherConfig.initialDelay;
    }

    @Override
    public Duration period() {
        return dispatcherConfig.period;
    }
}
