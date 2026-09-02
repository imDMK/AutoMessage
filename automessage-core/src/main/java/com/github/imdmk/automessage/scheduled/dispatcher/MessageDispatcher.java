package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageSender;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;
import com.github.imdmk.automessage.scheduled.selector.MessageSelector;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceContext;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.function.Supplier;

public final class MessageDispatcher {

    private final ScheduledMessageSender sender;
    private final Supplier<MessageSelector> selector;
    private final AudienceFilter filter;
    private final AudienceContext audienceContext;
    private final DispatchObserver observer;

    public MessageDispatcher(
            ScheduledMessageSender sender,
            Supplier<MessageSelector> selector,
            AudienceFilter filter,
            AudienceContext audienceContext,
            DispatchObserver observer
    ) {
        this.sender = sender;
        this.selector = selector;
        this.filter = filter;
        this.audienceContext = audienceContext;
        this.observer = observer;
    }

    public Optional<ScheduledMessage> dispatchNext(List<ScheduledMessage> messages, DispatchTarget target) {
        return dispatchNext(messages, target, true);
    }

    // Returns what it chose, so a caller that was asked to send something can say what went out.
    // The selector is the only thing that knows: a weighted or random channel draws afresh on
    // every call, so looking first and sending second would name a different message.
    public Optional<ScheduledMessage> dispatchNext(
            List<ScheduledMessage> messages,
            DispatchTarget target,
            boolean advanceSelectorIndex
    ) {
        if (messages.isEmpty()) {
            return Optional.empty();
        }

        final Optional<ScheduledMessage> chosen = selector.get().selectNext(messages, advanceSelectorIndex);
        chosen.ifPresent(message -> dispatch(message, target));

        return chosen;
    }

    public void dispatch(
            ScheduledMessage message,
            DispatchTarget target
    ) {
        // Which placeholders the message contains is a property of the message, not of who is
        // reading it, so the scan happens once here rather than once per recipient.
        final MessagePlaceholders placeholders = sender.placeholdersOf(message);
        final List<Viewer> recipients = new ArrayList<>();

        for (final Viewer viewer : target.recipients()) {
            if (filter.allows(viewer, message, audienceContext)) {
                recipients.add(viewer);
            }
        }

        // Handed over as one audience rather than one at a time, so a message that reads the
        // same for everybody is built once instead of once per player.
        sender.sendAll(recipients, message, placeholders);

        // Once per announcement, not once per recipient - and after the players have been served,
        // so nothing an observer does can delay what happens on the server.
        observer.onDispatched(message, placeholders);
    }
}
