package com.github.imdmk.automessage.scheduled.selector;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

final class RandomMessageSelector implements MessageSelector {

    @Override
    public Optional<ScheduledMessage> selectNext(List<ScheduledMessage> messages, boolean advanceIndex) {
        if (messages.isEmpty()) {
            return Optional.empty();
        }

        final int index = ThreadLocalRandom.current().nextInt(messages.size());
        return Optional.of(messages.get(index));
    }
}
