package com.github.imdmk.automessage.fabric;

import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import net.minecraft.stat.Stats;

import java.time.Duration;
import java.util.Optional;

public final class FabricPlaytimeSource implements PlaytimeSource {

    private static final long MILLIS_PER_TICK = 50L;

    @Override
    public Optional<Duration> playtimeOf(Viewer viewer) {
        if (!(viewer instanceof FabricViewer fabric)) {
            return Optional.empty();
        }

        return fabric.player()
                .map(player -> player.getStatHandler().getStat(Stats.CUSTOM, Stats.PLAY_TIME))
                .map(ticks -> Duration.ofMillis((long) ticks * MILLIS_PER_TICK));
    }
}
