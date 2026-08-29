package com.github.imdmk.automessage.scheduled.trigger;

import java.time.Duration;

/**
 * An event that sends a message, instead of the clock doing it.
 *
 * <p>
 * A message carrying a trigger is taken out of the timed rotation entirely: it fires when its
 * event happens and at no other time. That is what turns AutoMessage from a rotator of adverts
 * into something that can greet a first-time player or mark the moment the hundredth player logs
 * in.
 * </p>
 */
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

        /** Sent to a player as they join. */
        JOIN,

        /** Sent to a player the first time they ever join. */
        FIRST_JOIN,

        /** Broadcast to everyone the moment the online count reaches a threshold. */
        PLAYER_COUNT
    }
}
