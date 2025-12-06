package com.github.imdmk.automessage.scheduled.dispatcher;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.selector.MessageSelector;
import com.github.imdmk.automessage.shared.message.MessageService;
import com.github.imdmk.automessage.shared.validate.Validator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class MessageDispatcher {

    private final MessageService messageService;
    private final MessageSelector selector;
    private final AudienceFilter filter;
    private final Supplier<List<ScheduledMessage>> messagesSupplier;

    public MessageDispatcher(
            @NotNull MessageService messageService,
            @NotNull MessageSelector selector,
            @NotNull AudienceFilter filter,
            @NotNull Supplier<List<ScheduledMessage>> messagesSupplier
    ) {
        this.messageService = Validator.notNull(messageService, "messageService");
        this.selector = Validator.notNull(selector, "selector");
        this.filter = Validator.notNull(filter, "filter");
        this.messagesSupplier = Validator.notNull(messagesSupplier, "messages");
    }

    public void dispatchNext(@NotNull DispatchTarget target) {
        dispatchNext(target, true);
    }

    public void dispatchNext(
            @NotNull DispatchTarget target,
            boolean advanceSelectorIndex
    ) {
        final List<ScheduledMessage> messages = messagesSupplier.get();
        if (messages.isEmpty()) {
            return;
        }

        final Optional<ScheduledMessage> next = selector.selectNext(messages, advanceSelectorIndex);
        if (next.isEmpty()) {
            return;
        }

        dispatch(next.get(), target);
    }

    public void dispatch(
            @NotNull ScheduledMessage message,
            @NotNull DispatchTarget target
    ) {
        for (final Player recipient : target.recipients()) {
            if (!filter.allows(recipient, message)) {
                continue;
            }

            sendToPlayer(recipient, message);
        }
    }

    private void sendToPlayer(
            @NotNull Player recipient,
            @NotNull ScheduledMessage message
    ) {
        for (final Notice notice : message.notices()) {
            messageService.create()
                    .viewer(recipient)
                    .notice(notice)
                    .sendAsync();
        }
    }
}
