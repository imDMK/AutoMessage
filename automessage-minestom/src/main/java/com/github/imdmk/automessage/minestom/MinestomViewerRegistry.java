package com.github.imdmk.automessage.minestom;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import net.minestom.server.network.ConnectionManager;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public final class MinestomViewerRegistry implements ViewerRegistry {

    private final ConnectionManager connections;
    private final MinestomPermissions permissions;

    public MinestomViewerRegistry(ConnectionManager connections, MinestomPermissions permissions) {
        this.connections = connections;
        this.permissions = permissions;
    }

    @Override
    @Unmodifiable
    public Collection<Viewer> online() {
        // Copied rather than wrapped: the dispatcher walks this while players connect and leave.
        return connections.getOnlinePlayers().stream()
                .map(player -> (Viewer) new MinestomViewer(player, permissions))
                .toList();
    }

    @Override
    public int onlineCount() {
        return connections.getOnlinePlayerCount();
    }

    @Override
    public int maxPlayers() {
        return -1;
    }
}
