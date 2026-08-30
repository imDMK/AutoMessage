package com.github.imdmk.automessage.scheduled.selector;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Random selection biased by each message's {@code weight}.
 *
 * <p>
 * A message of weight 5 comes up five times as often as one of weight 1, which is how a server
 * promotes the vote reminder over the tips without letting it crowd them out entirely. Weight 0
 * excludes a message from the rotation without deleting it — useful for parking a seasonal
 * announcement until it is wanted again.
 * </p>
 */
final class WeightedMessageSelector implements MessageSelector {

    @Override
    public Optional<ScheduledMessage> selectNext(
            List<ScheduledMessage> messages,
            boolean advanceIndex
    ) {
        if (messages.isEmpty()) {
            return Optional.empty();
        }

        final long total = totalWeight(messages);

        // Every message parked at weight 0 leaves nothing to choose from; announcing one anyway
        // would defeat the point of parking them.
        if (total <= 0L) {
            return Optional.empty();
        }

        long target = ThreadLocalRandom.current().nextLong(total);

        for (int i = 0; i < messages.size(); i++) {
            final ScheduledMessage message = messages.get(i);
            target -= message.weight();

            if (target < 0L) {
                return Optional.of(message);
            }
        }

        // Unreachable while the weights above still sum to `total`.
        return Optional.of(messages.get(messages.size() - 1));
    }

    private static long totalWeight(List<ScheduledMessage> messages) {
        long total = 0L;

        for (int i = 0; i < messages.size(); i++) {
            total += messages.get(i).weight();
        }

        return total;
    }
}
