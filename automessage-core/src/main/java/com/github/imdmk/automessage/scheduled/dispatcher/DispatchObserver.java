package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;

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

    void onDispatched(ScheduledMessage message, MessagePlaceholders placeholders);

    /**
     * Releases whatever the observer holds open.
     *
     * <p>
     * An observer talking to the network owns threads, and those threads hold the plugin's class
     * loader alive. Leaving them running is how a plugin survives its own disable.
     * </p>
     */
    default void shutdown() {
    }

    static DispatchObserver none() {
        return (message, placeholders) -> {
        };
    }
}
