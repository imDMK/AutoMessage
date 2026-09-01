package com.github.imdmk.automessage.support;

import com.github.imdmk.automessage.platform.Platform;
import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// A whole server, as far as the plugin can tell. Everything a platform module implements is here
// in one place, so the core can be built and driven exactly as it is in production.
public final class TestPlatform implements Platform {

    public static final int MAX_PLAYERS = 20;

    private final Capabilities capabilities;
    private final TestScheduler scheduler = new TestScheduler();
    private final List<RecordingViewer> online = new ArrayList<>();

    private Duration playtime = Duration.ofHours(1);

    public TestPlatform(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    public static TestPlatform fullServer() {
        return new TestPlatform(Capabilities.all());
    }

    public TestScheduler scheduler() {
        return scheduler;
    }

    public TestPlatform join(RecordingViewer viewer) {
        online.add(viewer);
        return this;
    }

    public void leave(RecordingViewer viewer) {
        online.remove(viewer);
    }

    public void setPlaytime(Duration playtime) {
        this.playtime = playtime;
    }

    @Override
    public String name() {
        return "Test";
    }

    @Override
    public Capabilities capabilities() {
        return capabilities;
    }

    @Override
    public ViewerRegistry viewers() {
        return new ViewerRegistry() {

            @Override
            public Collection<Viewer> online() {
                return List.copyOf(online);
            }

            @Override
            public int onlineCount() {
                return online.size();
            }

            @Override
            public int maxPlayers() {
                return MAX_PLAYERS;
            }
        };
    }

    @Override
    public PlaytimeSource playtime() {
        return viewer -> Optional.of(playtime);
    }
}
