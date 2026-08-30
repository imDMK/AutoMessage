package com.github.imdmk.automessage.scheduled.trigger;

/**
 * Fires once when the online player count reaches a threshold.
 *
 * <p>
 * Milestones like "100 players online for the first time today" only land if they are announced at
 * the moment they happen.
 * </p>
 *
 * @param threshold online count that triggers the message, inclusive
 */
public record PlayerCountTrigger(int threshold) implements MessageTrigger {

    public PlayerCountTrigger {
        if (threshold < 1) {
            throw new IllegalArgumentException("threshold must be at least 1, got " + threshold);
        }
    }

    @Override
    public Type type() {
        return Type.PLAYER_COUNT;
    }
}
