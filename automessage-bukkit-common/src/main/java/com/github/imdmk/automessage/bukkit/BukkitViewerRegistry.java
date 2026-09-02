package com.github.imdmk.automessage.bukkit;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import net.kyori.adventure.platform.AudienceProvider;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class BukkitViewerRegistry implements ViewerRegistry {

    private final Server server;
    private final AudienceProvider audiences;

    public BukkitViewerRegistry(Server server, AudienceProvider audiences) {
        this.server = server;
        this.audiences = audiences;
    }

    @Override
    public Collection<Viewer> online() {
        final Collection<? extends Player> players = server.getOnlinePlayers();
        final List<Viewer> viewers = new ArrayList<>(players.size());

        for (final Player player : players) {
            viewers.add(new BukkitViewer(player, audiences));
        }

        return List.copyOf(viewers);
    }

    @Override
    public int onlineCount() {
        return server.getOnlinePlayers().size();
    }

    @Override
    public int maxPlayers() {
        return server.getMaxPlayers();
    }
}
