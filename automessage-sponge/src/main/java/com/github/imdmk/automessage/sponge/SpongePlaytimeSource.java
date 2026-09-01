package com.github.imdmk.automessage.sponge;

import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.statistic.Statistics;

import java.time.Duration;
import java.util.Optional;

public final class SpongePlaytimeSource implements PlaytimeSource {

    private static final long MILLIS_PER_TICK = 50L;

    @Override
    public Optional<Duration> playtimeOf(Viewer viewer) {
        if (!(viewer instanceof SpongeViewer sponge)) {
            return Optional.empty();
        }

        return sponge.player()
                .flatMap(player -> player.get(Keys.STATISTICS))
                .map(statistics -> statistics.get(Statistics.PLAY_TIME.get()))
                .map(ticks -> Duration.ofMillis(ticks * MILLIS_PER_TICK));
    }
}
