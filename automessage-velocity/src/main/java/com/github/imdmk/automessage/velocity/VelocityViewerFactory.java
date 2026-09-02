package com.github.imdmk.automessage.velocity;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import com.velocitypowered.api.command.CommandSource;

public final class VelocityViewerFactory implements ViewerFactory<CommandSource> {

    @Override
    public Viewer of(CommandSource source) {
        return new VelocityViewer(source);
    }
}
