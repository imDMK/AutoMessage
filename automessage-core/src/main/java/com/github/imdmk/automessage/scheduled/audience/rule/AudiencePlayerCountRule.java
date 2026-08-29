package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;

/**
 * Restricts a message to a range of online player counts.
 *
 * <p>
 * The point is announcements that only make sense at a given population: "quiet right now — invite
 * a friend" below ten players, "the server is filling up" above eighty. Both read as noise at the
 * wrong moment, which is why servers usually cannot use them at all.
 * </p>
 *
 * @param minimum lowest online count that still receives the message, inclusive
 * @param maximum highest online count that still receives it, inclusive;
 *                {@link Integer#MAX_VALUE} leaves the range open at the top
 */
public record AudiencePlayerCountRule(int minimum, int maximum) implements AudienceRule {

    public static final int UNBOUNDED = Integer.MAX_VALUE;

    public AudiencePlayerCountRule {
        if (minimum < 0) {
            throw new IllegalArgumentException("minimum must not be negative, got " + minimum);
        }

        if (maximum < minimum) {
            throw new IllegalArgumentException(
                    "maximum (" + maximum + ") must not be below minimum (" + minimum + ")"
            );
        }
    }

    public static AudiencePlayerCountRule atLeast(int minimum) {
        return new AudiencePlayerCountRule(minimum, UNBOUNDED);
    }

    public static AudiencePlayerCountRule atMost(int maximum) {
        return new AudiencePlayerCountRule(0, maximum);
    }

    @Override
    public boolean test(Player player) {
        final int online = player.getServer().getOnlinePlayers().size();
        return online >= minimum && online <= maximum;
    }

    @Override
    public Type type() {
        return Type.PLAYER_COUNT;
    }
}
