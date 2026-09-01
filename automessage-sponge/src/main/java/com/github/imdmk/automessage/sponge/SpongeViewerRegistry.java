package com.github.imdmk.automessage.sponge;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import org.jetbrains.annotations.Unmodifiable;
import org.spongepowered.api.Game;

import java.util.Collection;
import java.util.List;

public final class SpongeViewerRegistry implements ViewerRegistry {

    private final Game game;

    public SpongeViewerRegistry(Game game) {
        this.game = game;
    }

    @Override
    @Unmodifiable
    public Collection<Viewer> online() {
        if (!game.isServerAvailable()) {
            return List.of();
        }

        // Copied rather than wrapped: the dispatcher walks this while players join and leave.
        return game.server().onlinePlayers().stream()
                .map(player -> (Viewer) SpongeViewer.of(player))
                .toList();
    }

    @Override
    public int onlineCount() {
        return game.isServerAvailable() ? game.server().onlinePlayers().size() : 0;
    }

    @Override
    public int maxPlayers() {
        return game.isServerAvailable() ? game.server().maxPlayers() : -1;
    }
}
