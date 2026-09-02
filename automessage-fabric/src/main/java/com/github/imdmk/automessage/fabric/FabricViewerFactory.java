package com.github.imdmk.automessage.fabric;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import net.minecraft.server.command.ServerCommandSource;

public final class FabricViewerFactory implements ViewerFactory<ServerCommandSource> {

    private final FabricServerHolder holder;

    FabricViewerFactory(FabricServerHolder holder) {
        this.holder = holder;
    }

    @Override
    public Viewer of(ServerCommandSource source) {
        return FabricViewer.of(holder.audiences(), source);
    }
}
