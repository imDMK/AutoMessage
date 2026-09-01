package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;

import java.util.List;

public interface DispatchObserver {

    void onDispatched(ScheduledMessage message, MessagePlaceholders placeholders);

    default void shutdown() {
    }

    // Order matters: an observer that throws stops the ones behind it, so the cheap local one
    // goes first and anything reaching the network goes last.
    static DispatchObserver of(DispatchObserver... observers) {
        final List<DispatchObserver> all = List.of(observers);

        return new DispatchObserver() {
            @Override
            public void onDispatched(ScheduledMessage message, MessagePlaceholders placeholders) {
                for (final DispatchObserver observer : all) {
                    observer.onDispatched(message, placeholders);
                }
            }

            @Override
            public void shutdown() {
                for (final DispatchObserver observer : all) {
                    observer.shutdown();
                }
            }
        };
    }

    static DispatchObserver none() {
        return (message, placeholders) -> {
        };
    }
}
