package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;

/**
 * Notified once per announcement, after it has gone out to the players.
 *
 * <p>
 * Called once per message rather than once per recipient: an observer mirroring announcements
 * elsewhere wants the announcement, not one copy of it per player online.
 * </p>
 */
@FunctionalInterface
public interface DispatchObserver {

    void onDispatched(ScheduledMessage message);

    static DispatchObserver none() {
        return message -> {
        };
    }
}
