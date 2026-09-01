package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;

@FunctionalInterface
public interface DispatchObserver {

    void onDispatched(ScheduledMessage message, MessagePlaceholders placeholders);

    default void shutdown() {
    }

    static DispatchObserver none() {
        return (message, placeholders) -> {
        };
    }
}
