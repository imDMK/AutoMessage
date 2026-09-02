package com.github.imdmk.automessage.notice;

import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

public record BossBarPart(
        String message,
        Duration duration,
        BossBar.Color color,
        BossBar.Overlay overlay,
        @Nullable Double progress
) implements NoticePart {

    public static final String KEY = "bossbar";

    public static final BossBar.Color DEFAULT_COLOR = BossBar.Color.WHITE;
    public static final BossBar.Overlay DEFAULT_OVERLAY = BossBar.Overlay.PROGRESS;
    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(5);

    public BossBarPart {
        Objects.requireNonNull(message, "message");

        duration = duration == null ? DEFAULT_DURATION : duration;
        color = color == null ? DEFAULT_COLOR : color;
        overlay = overlay == null ? DEFAULT_OVERLAY : overlay;

        if (duration.isNegative()) {
            throw new IllegalArgumentException("bossbar duration must not be negative");
        }

        if (progress != null && (progress < 0.0D || progress > 1.0D)) {
            throw new IllegalArgumentException("bossbar progress must be between 0.0 and 1.0, got " + progress);
        }
    }

    public float progressOrFull() {
        return progress == null ? BossBar.MAX_PROGRESS : progress.floatValue();
    }

    @Override
    public String key() {
        return KEY;
    }
}
