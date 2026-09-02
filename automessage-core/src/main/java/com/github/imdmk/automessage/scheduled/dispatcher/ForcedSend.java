package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;

/**
 * What came of sending a channel's next announcement by hand.
 */
public record ForcedSend(Kind kind, Optional<ScheduledMessage> message, @Nullable Duration nextIn) {

    public enum Kind {
        SENT,
        NOBODY_ONLINE,
        NO_MESSAGES,
        DISABLED
    }

    static ForcedSend of(MessageDispatcherTask.Outcome outcome, Duration nextIn) {
        final Kind kind = switch (outcome.kind()) {
            case SENT -> Kind.SENT;
            case NOBODY_ONLINE -> Kind.NOBODY_ONLINE;
            case NO_MESSAGES -> Kind.NO_MESSAGES;
        };

        return new ForcedSend(kind, outcome.message(), nextIn);
    }

    // Named in the file but switched off, which is a different answer from a name nobody set up.
    static ForcedSend disabled() {
        return new ForcedSend(Kind.DISABLED, Optional.empty(), null);
    }
}
