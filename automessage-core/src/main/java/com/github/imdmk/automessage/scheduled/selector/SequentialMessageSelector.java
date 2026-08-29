package com.github.imdmk.automessage.scheduled.selector;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

final class SequentialMessageSelector implements MessageSelector {

    private static final int RESET_THRESHOLD = 1_000_000_000;
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public Optional<ScheduledMessage> selectNext(
            List<ScheduledMessage> messages,
            boolean advanceIndex
    ) {
        final int size = messages.size();
        if (size == 0) {
            return Optional.empty();
        }

        // A preview and a scheduled tick can land here at the same time, so the position has to
        // be read and advanced in one step — a separate get/set pair lets both pick the same
        // message and then skip the next one.
        final int current = advanceIndex
                ? index.getAndUpdate(value -> value + 1 >= RESET_THRESHOLD ? 0 : value + 1)
                : index.get();

        return Optional.of(messages.get(Math.floorMod(current, size)));
    }
}

