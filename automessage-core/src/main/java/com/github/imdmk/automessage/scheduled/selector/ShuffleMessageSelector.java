package com.github.imdmk.automessage.scheduled.selector;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Random order, but every message is shown once before any is repeated.
 *
 * <p>
 * {@code RANDOM} draws independently each time, so it can show the same announcement three times
 * running while another waits ten rounds for its turn. Players read that as a broken plugin. This
 * selector deals the messages out like a shuffled deck instead: unpredictable order, no repeats
 * until the deck is exhausted, then a fresh shuffle.
 * </p>
 */
final class ShuffleMessageSelector implements MessageSelector {

    /** Remaining messages of the current deck, consumed from the end. */
    private final List<ScheduledMessage> deck = new ArrayList<>();

    /**
     * The message list the current deck was dealt from.
     *
     * <p>
     * A reload can add, remove or reorder messages. Carrying on with a deck built from the old
     * list would keep announcing entries the administrator has just deleted, so the deck is
     * re-dealt whenever the source no longer matches.
     * </p>
     */
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
