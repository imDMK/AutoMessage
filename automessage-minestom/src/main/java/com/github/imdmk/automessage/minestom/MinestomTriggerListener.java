package com.github.imdmk.automessage.minestom;

import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import com.github.imdmk.automessage.scheduled.trigger.MessageTriggerService;
import net.minestom.server.command.CommandSender;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.network.ConnectionManager;

public final class MinestomTriggerListener {

    private final ConnectionManager connections;
    private final ViewerFactory<CommandSender> viewers;
    private final MessageTriggerService triggerService;

    public MinestomTriggerListener(
            ConnectionManager connections,
            ViewerFactory<CommandSender> viewers,
            MessageTriggerService triggerService
    ) {
        this.connections = connections;
        this.viewers = viewers;
        this.triggerService = triggerService;
    }

    public void register(EventNode<net.minestom.server.event.Event> events) {
        events.addListener(PlayerSpawnEvent.class, this::onSpawn);
        events.addListener(PlayerDisconnectEvent.class, this::onDisconnect);
    }

    private void onSpawn(PlayerSpawnEvent event) {
        // Fires again on every world change; only the first spawn of a connection is a join.
        if (!event.isFirstSpawn()) {
            return;
        }

        triggerService.onJoin(viewers.of(event.getPlayer()), false);
        triggerService.onPlayerCountChanged(connections.getOnlinePlayerCount());
    }

    private void onDisconnect(PlayerDisconnectEvent event) {
        // The leaving player is still counted here, so the remaining population is one lower.
        final int remaining = connections.getOnlinePlayerCount() - 1;

        triggerService.onPlayerCountChanged(Math.max(0, remaining));
    }
}
