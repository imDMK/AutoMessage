package com.github.imdmk.automessage.scheduled.trigger;

import java.time.Duration;

public sealed interface MessageTrigger permits JoinTrigger, PlayerCountTrigger {

    static JoinTrigger join(Duration delay) {
        return new JoinTrigger(delay, false);
    }

    static JoinTrigger firstJoin(Duration delay) {
        return new JoinTrigger(delay, true);
    }

    static PlayerCountTrigger playerCount(int threshold) {
        return new PlayerCountTrigger(threshold);
    }

    Type type();

    enum Type {

        JOIN,

        FIRST_JOIN,

        PLAYER_COUNT
    }
}
