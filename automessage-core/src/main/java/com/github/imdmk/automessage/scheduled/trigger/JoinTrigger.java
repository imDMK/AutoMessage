package com.github.imdmk.automessage.scheduled.trigger;

import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * Fires for the player who just joined.
 *
 * @param delay          how long to wait after the join before sending; the join itself floods
 *                       chat with the server's own messages, and an announcement landing in the
 *                       middle of that is simply not read
 * @param firstJoinOnly  restricts the message to players the server has never seen before
 */
public record JoinTrigger(Duration delay, boolean firstJoinOnly) implements MessageTrigger {

    public JoinTrigger {
        if (delay == null || delay.isNegative()) {
            delay = Duration.ZERO;
        }
    }

    public boolean appliesTo(Player player) {
        return !firstJoinOnly || !player.hasPlayedBefore();
    }

    @Override
    public Type type() {
        return firstJoinOnly ? Type.FIRST_JOIN : Type.JOIN;
    }
}
