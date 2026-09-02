package com.github.imdmk.automessage.sponge;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import org.spongepowered.api.command.CommandCause;

public final class SpongeViewerFactory implements ViewerFactory<CommandCause> {

    @Override
    public Viewer of(CommandCause cause) {
        return SpongeViewer.of(cause);
    }
}
