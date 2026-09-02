package com.github.imdmk.automessage.sponge;

import com.github.imdmk.automessage.scheduled.trigger.MessageTriggerService;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public final class SpongeTriggerListener {

    private final Game game;
    private final MessageTriggerService triggerService;

    public SpongeTriggerListener(Game game, MessageTriggerService triggerService) {
        this.game = game;
        this.triggerService = triggerService;
    }

    @Listener(order = Order.POST)
    public void onJoin(ServerSideConnectionEvent.Join event) {
        final ServerPlayer player = event.player();

        triggerService.onJoin(SpongeViewer.of(player), !player.hasPlayedBefore());

        // The joining player is already in the server's collection by the time this fires.
        triggerService.onPlayerCountChanged(game.server().onlinePlayers().size());
    }

    @Listener(order = Order.POST)
    public void onLeave(ServerSideConnectionEvent.Leave event) {
        // The leaving player is still counted here, so the post-quit population is one lower.
        // Milestones need that number to rearm at the right moment.
        final int remaining = game.server().onlinePlayers().size() - 1;

        triggerService.onPlayerCountChanged(Math.max(0, remaining));
    }
}
