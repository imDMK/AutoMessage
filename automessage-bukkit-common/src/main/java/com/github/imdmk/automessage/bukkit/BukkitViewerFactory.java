package com.github.imdmk.automessage.bukkit;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import net.kyori.adventure.platform.AudienceProvider;
import org.bukkit.command.CommandSender;

public final class BukkitViewerFactory implements ViewerFactory<CommandSender> {

    private final AudienceProvider audiences;

    public BukkitViewerFactory(AudienceProvider audiences) {
        this.audiences = audiences;
    }

    @Override
    public Viewer of(CommandSender sender) {
        return new BukkitViewer(sender, audiences);
    }
}
