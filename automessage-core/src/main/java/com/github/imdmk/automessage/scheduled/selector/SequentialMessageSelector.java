package com.github.imdmk.automessage.scheduled.selector;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

final class SequentialMessageSelector implements MessageSelector {

    private static final int RESET_THRESHOLD = 1_000_000_000;
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public Optional<ScheduledMessage> selectNext(
            @NotNull List<ScheduledMessage> messages,
            boolean advanceIndex
    ) {
        final int size = messages.size();
        if (size == 0) {
            return Optional.empty();
        }

        final int current = index.get();
        final int position = Math.floorMod(current, size);
        final ScheduledMessage selected = messages.get(position);

        // increment AFTER selecting, if required
        if (advanceIndex) {
            int next = current + 1;

            if (next >= RESET_THRESHOLD) {
                next = 0; // prevent overflow
            }

            index.set(next);
        }

        return Optional.of(selected);
    }
}

