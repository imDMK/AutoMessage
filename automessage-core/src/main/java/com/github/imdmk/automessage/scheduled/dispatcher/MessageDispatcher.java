package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.ScheduledMessageSender;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;
import com.github.imdmk.automessage.scheduled.selector.MessageSelector;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public final class MessageDispatcher {

    private final ScheduledMessageSender sender;
    private final Supplier<MessageSelector> selector;
    private final AudienceFilter filter;
    private final ScheduledMessageRepository repository;

    public MessageDispatcher(
            ScheduledMessageSender sender,
            Supplier<MessageSelector> selector,
            AudienceFilter filter,
            ScheduledMessageRepository repository
    ) {
        this.sender = sender;
        this.selector = selector;
        this.filter = filter;
        this.repository = repository;
    }

    public void dispatchNext(DispatchTarget target) {
        dispatchNext(target, true);
    }

    public void dispatchNext(
            DispatchTarget target,
            boolean advanceSelectorIndex
    ) {
        final List<ScheduledMessage> messages = repository.findAll();
        if (messages.isEmpty()) {
            return;
        }

        selector.get()
                .selectNext(messages, advanceSelectorIndex)
                .ifPresent(message -> dispatch(message, target));
    }

    public void dispatch(
            ScheduledMessage message,
            DispatchTarget target
    ) {
        // Which placeholders the message contains is a property of the message, not of who is
        // reading it, so the scan happens once here rather than once per recipient.
        final MessagePlaceholders placeholders = sender.placeholdersOf(message);

        for (final Player player : target.recipients()) {
            if (filter.allows(player, message)) {
                sender.sendAsync(player, message, placeholders);
            }
        }
    }
}
