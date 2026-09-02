package com.github.imdmk.automessage.fabric;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;

public final class FabricViewerRegistry implements ViewerRegistry {

    private final FabricServerHolder holder;

    FabricViewerRegistry(FabricServerHolder holder) {
        this.holder = holder;
    }

    @Override
    @Unmodifiable
    public Collection<Viewer> online() {
        final MinecraftServer server = holder.server();
        if (server == null) {
            return List.of();
        }

        // Copied rather than wrapped: the player list is the server's own, and it changes as
        // people join and leave while the dispatcher is walking it.
        return server.getPlayerManager().getPlayerList().stream()
                .map(player -> (Viewer) FabricViewer.of(holder.audiences(), player))
                .toList();
    }

    @Override
    public int onlineCount() {
        final MinecraftServer server = holder.server();
        return server == null ? 0 : server.getPlayerManager().getCurrentPlayerCount();
    }

    @Override
    public int maxPlayers() {
        final MinecraftServer server = holder.server();
        return server == null ? -1 : server.getPlayerManager().getMaxPlayerCount();
    }
}
