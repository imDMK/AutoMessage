package com.github.imdmk.automessage.minestom;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import net.minestom.server.command.CommandSender;

public final class MinestomViewerFactory implements ViewerFactory<CommandSender> {

    private final MinestomPermissions permissions;

    public MinestomViewerFactory(MinestomPermissions permissions) {
        this.permissions = permissions;
    }

    @Override
    public Viewer of(CommandSender sender) {
        return new MinestomViewer(sender, permissions);
    }
}
