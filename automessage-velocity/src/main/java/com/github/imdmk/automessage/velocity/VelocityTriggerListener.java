package com.github.imdmk.automessage.velocity;

import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import com.github.imdmk.automessage.scheduled.trigger.MessageTriggerService;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.ProxyServer;

public final class VelocityTriggerListener {

    private final ProxyServer proxy;
    private final ViewerFactory<CommandSource> viewers;
    private final MessageTriggerService triggerService;

    public VelocityTriggerListener(
            ProxyServer proxy,
            ViewerFactory<CommandSource> viewers,
            MessageTriggerService triggerService
    ) {
        this.proxy = proxy;
        this.viewers = viewers;
        this.triggerService = triggerService;
    }

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        triggerService.onJoin(viewers.of(event.getPlayer()), false);

        // PostLoginEvent fires once the player is counted, so this is the population including them.
        triggerService.onPlayerCountChanged(proxy.getPlayerCount());
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        // Unlike Bukkit's quit event, the player is already gone from the count here.
        triggerService.onPlayerCountChanged(proxy.getPlayerCount());
    }
}
