package com.github.imdmk.automessage.platform.viewer;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public interface ViewerRegistry {

    @Unmodifiable
    Collection<Viewer> online();

    int onlineCount();

    int maxPlayers();
}
