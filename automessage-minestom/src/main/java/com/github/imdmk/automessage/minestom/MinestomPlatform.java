package com.github.imdmk.automessage.minestom;

import com.github.imdmk.automessage.platform.Platform;
import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.capability.Capability;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;

import java.util.EnumSet;
import java.util.Set;

public final class MinestomPlatform implements Platform {

    private final TaskScheduler scheduler;
    private final ViewerRegistry viewers;
    private final boolean realPermissions;

    public MinestomPlatform(TaskScheduler scheduler, ViewerRegistry viewers, boolean realPermissions) {
        this.scheduler = scheduler;
        this.viewers = viewers;
        this.realPermissions = realPermissions;
    }

    @Override
    public String name() {
        return "Minestom";
    }

    @Override
    public Capabilities capabilities() {
        final Set<Capability> supported = EnumSet.of(
                Capability.SOUND_NOTICE,
                Capability.BOSSBAR_NOTICE,
                Capability.TITLE_NOTICE
        );

        if (realPermissions) {
            supported.add(Capability.PERMISSION_RULE);
            supported.add(Capability.GROUP_RULE);
        }

        return new Capabilities(supported);
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
