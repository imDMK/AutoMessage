package com.github.imdmk.automessage.bukkit;

import com.github.imdmk.automessage.platform.Platform;
import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import net.kyori.adventure.platform.AudienceProvider;
import org.bukkit.Server;

public final class BukkitPlatform implements Platform {

    private final String name;
    private final TaskScheduler scheduler;
    private final ViewerRegistry viewers;
    private final PlaytimeSource playtime;

    public BukkitPlatform(String name, Server server, AudienceProvider audiences, TaskScheduler scheduler) {
        this.name = name;
        this.scheduler = scheduler;
        this.viewers = new BukkitViewerRegistry(server, audiences);
        this.playtime = new BukkitPlaytimeSource();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Capabilities capabilities() {
        return Capabilities.all();
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
