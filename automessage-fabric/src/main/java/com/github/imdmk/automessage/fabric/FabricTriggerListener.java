package com.github.imdmk.automessage.fabric;

import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import com.github.imdmk.automessage.scheduled.trigger.MessageTriggerService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;

public final class FabricTriggerListener {

    private final MinecraftServer server;
    private final ViewerFactory<ServerCommandSource> viewers;
    private final MessageTriggerService triggerService;

    public FabricTriggerListener(
            MinecraftServer server,
            ViewerFactory<ServerCommandSource> viewers,
            MessageTriggerService triggerService
    ) {
        this.server = server;
        this.viewers = viewers;
        this.triggerService = triggerService;
    }

    public void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, ignored) -> onJoin(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, ignored) -> onDisconnect());
    }

    private void onJoin(ServerPlayerEntity player) {
        triggerService.onJoin(viewers.of(player.getCommandSource()), isFirstJoin(player));

        // The joining player is already on the server's list by the time this fires.
        triggerService.onPlayerCountChanged(server.getPlayerManager().getCurrentPlayerCount());
    }

    private void onDisconnect() {
        // The leaving player is still counted here, so the post-quit population is one lower.
        // Milestones need that number to rearm at the right moment.
        final int remaining = server.getPlayerManager().getCurrentPlayerCount() - 1;

        triggerService.onPlayerCountChanged(Math.max(0, remaining));
    }

    private boolean isFirstJoin(ServerPlayerEntity player) {
        return player.getStatHandler().getStat(Stats.CUSTOM, Stats.PLAY_TIME) == 0;
    }
}
