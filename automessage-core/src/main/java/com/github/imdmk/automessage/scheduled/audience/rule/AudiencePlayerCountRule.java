package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.Viewer;

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

    @Override
    public boolean test(Viewer viewer, AudienceContext context) {
        final int online = context.viewers().onlineCount();
        return online >= minimum && online <= maximum;
    }

    @Override
    public Type type() {
        return Type.PLAYER_COUNT;
    }
}
