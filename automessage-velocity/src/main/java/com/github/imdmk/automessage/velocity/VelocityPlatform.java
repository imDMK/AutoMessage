package com.github.imdmk.automessage.velocity;

import com.github.imdmk.automessage.platform.Platform;
import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.capability.Capability;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import com.velocitypowered.api.proxy.ProxyServer;

public final class VelocityPlatform implements Platform {

    private final TaskScheduler scheduler;
    private final ViewerRegistry viewers;

    public VelocityPlatform(ProxyServer proxy, TaskScheduler scheduler) {
        this.scheduler = scheduler;
        this.viewers = new VelocityViewerRegistry(proxy);
    }

    @Override
    public String name() {
        return "Velocity";
    }

    @Override
    public Capabilities capabilities() {
        return Capabilities.of(
                Capability.PERMISSION_RULE,
                Capability.GROUP_RULE,
                Capability.SOUND_NOTICE,
                Capability.BOSSBAR_NOTICE,
                Capability.TITLE_NOTICE,
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
        return PlaytimeSource.unavailable();
    }
}
