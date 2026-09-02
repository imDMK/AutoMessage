package com.github.imdmk.automessage.bukkit;

import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

public final class BukkitPlaytimeSource implements PlaytimeSource {

    private static final long MILLIS_PER_TICK = 50L;

    @Override
    public Optional<Duration> playtimeOf(Viewer viewer) {
        if (!(viewer instanceof BukkitViewer bukkit) || !(bukkit.sender() instanceof Player player)) {
            return Optional.empty();
        }

        return Optional.of(Duration.ofMillis(
                (long) player.getStatistic(Statistic.PLAY_ONE_MINUTE) * MILLIS_PER_TICK
        ));
    }
}
