package com.github.imdmk.automessage.velocity;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public final class VelocityViewerRegistry implements ViewerRegistry {

    private final ProxyServer proxy;

    public VelocityViewerRegistry(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    @Unmodifiable
    public Collection<Viewer> online() {
        // Copied rather than wrapped: the dispatcher walks this off the netty threads that add and
        // remove players, and Velocity's own collection is a live view.
        return proxy.getAllPlayers().stream()
                .map(player -> (Viewer) new VelocityViewer(player))
                .toList();
    }

    @Override
    public int onlineCount() {
        return proxy.getPlayerCount();
    }

    @Override
    public int maxPlayers() {
        return proxy.getConfiguration().getShowMaxPlayers();
    }
}
