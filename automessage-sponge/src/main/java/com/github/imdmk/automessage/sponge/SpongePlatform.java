package com.github.imdmk.automessage.sponge;

import com.github.imdmk.automessage.platform.Platform;
import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.capability.Capability;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import org.spongepowered.api.Game;

public final class SpongePlatform implements Platform {

    private final TaskScheduler scheduler;
    private final ViewerRegistry viewers;
    private final PlaytimeSource playtime = new SpongePlaytimeSource();

    public SpongePlatform(Game game, TaskScheduler scheduler) {
        this.scheduler = scheduler;
        this.viewers = new SpongeViewerRegistry(game);
    }

    @Override
    public String name() {
        return "Sponge";
    }

    @Override
    public Capabilities capabilities() {
        return Capabilities.allExcept(Capability.EXTERNAL_PLACEHOLDERS);
    }

    @Override
    public ViewerRegistry viewers() {
        return viewers;
    }

    @Override
    public TaskScheduler scheduler() {
        return scheduler;
    }

    @Override
    public PlaytimeSource playtime() {
        return playtime;
    }
}
