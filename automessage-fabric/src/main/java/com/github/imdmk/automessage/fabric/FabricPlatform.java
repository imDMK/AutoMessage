package com.github.imdmk.automessage.fabric;

import com.github.imdmk.automessage.platform.Platform;
import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.capability.Capability;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;

public final class FabricPlatform implements Platform {

    private final TaskScheduler scheduler;
    private final ViewerRegistry viewers;
    private final PlaytimeSource playtime = new FabricPlaytimeSource();

    public FabricPlatform(TaskScheduler scheduler, ViewerRegistry viewers) {
        this.scheduler = scheduler;
        this.viewers = viewers;
    }

    @Override
    public String name() {
        return "Fabric";
    }

    @Override
    public Capabilities capabilities() {
        return Capabilities.allExcept(
                Capability.EXTERNAL_PLACEHOLDERS,
                Capability.METRICS
        );
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
