package com.github.imdmk.automessage.scheduled.trigger;

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
