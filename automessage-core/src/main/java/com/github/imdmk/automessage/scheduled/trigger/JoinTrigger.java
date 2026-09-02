package com.github.imdmk.automessage.scheduled.trigger;

import java.time.Duration;

// firstJoin is passed in rather than asked of the viewer: "has played here before" is spelled
// differently on every platform, and some cannot answer it at all.
public record JoinTrigger(Duration delay, boolean firstJoinOnly) implements MessageTrigger {

    public JoinTrigger {
        if (delay == null || delay.isNegative()) {
            delay = Duration.ZERO;
        }
    }

    public boolean appliesTo(boolean firstJoin) {
        return !firstJoinOnly || firstJoin;
    }

    @Override
    public Type type() {
        return firstJoinOnly ? Type.FIRST_JOIN : Type.JOIN;
    }
}
