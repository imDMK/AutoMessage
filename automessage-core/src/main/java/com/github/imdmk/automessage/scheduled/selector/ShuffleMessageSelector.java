package com.github.imdmk.automessage.scheduled.selector;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

final class ShuffleMessageSelector implements MessageSelector {

    private final List<ScheduledMessage> deck = new ArrayList<>();

    // A reload can add, remove or reorder messages; carrying on with a deck dealt from the old
    // list would keep announcing entries the administrator has just deleted.
    private List<ScheduledMessage> dealtFrom = List.of();

    @Override
    public synchronized Optional<ScheduledMessage> selectNext(
            List<ScheduledMessage> messages,
            boolean advanceIndex
    ) {
        if (messages.isEmpty()) {
            deck.clear();
            dealtFrom = List.of();
            return Optional.empty();
        }

        if (deck.isEmpty() || !dealtFrom.equals(messages)) {
            deal(messages);
        }

        final ScheduledMessage next = deck.get(deck.size() - 1);

        // A preview looks at the next card without taking it, so the rotation a player sees is
        // unaffected by staff checking how a message renders.
        if (advanceIndex) {
            deck.remove(deck.size() - 1);
        }

        return Optional.of(next);
    }

    private void deal(List<ScheduledMessage> messages) {
        dealtFrom = List.copyOf(messages);

        deck.clear();
        deck.addAll(dealtFrom);

        Collections.shuffle(deck, ThreadLocalRandom.current());
    }
}
