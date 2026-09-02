package com.github.imdmk.automessage.platform.viewer;

import java.time.Duration;
import java.util.Optional;

@FunctionalInterface
public interface PlaytimeSource {

    Optional<Duration> playtimeOf(Viewer viewer);

    static PlaytimeSource unavailable() {
        return viewer -> Optional.empty();
    }
}
