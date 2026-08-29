package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * Restricts a message to players whose time on the server falls in a range.
 *
 * <p>
 * Newcomer tips stop reaching veterans, and "thanks for sticking around" stops reaching people who
 * joined a minute ago. The figure comes from the server's own {@code PLAY_ONE_MINUTE} statistic —
 * despite the name it counts ticks — so it needs no database of our own and matches whatever the
 * server already reports.
 * </p>
 *
 * @param minimum lowest playtime that still receives the message, inclusive
 * @param maximum highest playtime that still receives it, inclusive; {@code null} leaves the range
 *                open at the top
 */
public record AudiencePlayTimeRule(Duration minimum, @Nullable Duration maximum) implements AudienceRule {

    private static final long MILLIS_PER_TICK = 50L;

    public AudiencePlayTimeRule {
        if (minimum == null || minimum.isNegative()) {
            throw new IllegalArgumentException("minimum must be a non-negative duration");
        }

        if (maximum != null && maximum.compareTo(minimum) < 0) {
            throw new IllegalArgumentException("maximum must not be below minimum");
        }
    }

    public static AudiencePlayTimeRule atLeast(Duration minimum) {
        return new AudiencePlayTimeRule(minimum, null);
    }

    public static AudiencePlayTimeRule below(Duration maximum) {
        return new AudiencePlayTimeRule(Duration.ZERO, maximum);
    }

    @Override
    public boolean test(Player player) {
        final Duration played = Duration.ofMillis(
                (long) player.getStatistic(Statistic.PLAY_ONE_MINUTE) * MILLIS_PER_TICK
        );

        if (played.compareTo(minimum) < 0) {
            return false;
        }

        return maximum == null || played.compareTo(maximum) <= 0;
    }

    @Override
    public Type type() {
        return Type.PLAYTIME;
    }
}
