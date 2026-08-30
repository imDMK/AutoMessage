package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.platform.scheduler.PluginTask;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * The repeating tick of one announcement channel.
 */
public final class MessageDispatcherTask implements PluginTask {

    private final Server server;
    private final BooleanSupplier masterSwitch;
    private final AnnouncementChannel channel;
    private final ScheduledMessageRepository repository;
    private final MessageDispatcher messageDispatcher;
    private final DispatchTiming timing;

    public MessageDispatcherTask(
            Server server,
            BooleanSupplier masterSwitch,
            AnnouncementChannel channel,
            ScheduledMessageRepository repository,
            MessageDispatcher messageDispatcher,
            DispatchTiming timing
    ) {
        this.server = server;
        this.masterSwitch = masterSwitch;
        this.channel = channel;
        this.repository = repository;
        this.messageDispatcher = messageDispatcher;
        this.timing = timing;
    }

    @Override
    public void run() {
        // The master switch is read each tick rather than at schedule time, so /automessage
        // disable takes effect without tearing down and rebuilding every channel's task.
        if (!masterSwitch.getAsBoolean()) {
            return;
        }

        final Collection<? extends Player> onlinePlayers = server.getOnlinePlayers();
        if (onlinePlayers.isEmpty()) {
            return;
        }

        final List<ScheduledMessage> messages = repository.findByChannel(channel);
        if (messages.isEmpty()) {
            return;
        }

        messageDispatcher.dispatchNext(messages, DispatchTarget.players(onlinePlayers));
    }

    public AnnouncementChannel channel() {
        return channel;
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
