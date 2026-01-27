package com.github.imdmk.automessage.scheduled.dispatcher;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.selector.MessageSelector;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public final class MessageDispatcher {

    private final MessageService messageService;
    private final MessageSelector selector;
    private final AudienceFilter filter;
    private final Supplier<List<ScheduledMessage>> messages;

    public MessageDispatcher(
            MessageService messageService,
            MessageSelector selector,
            AudienceFilter filter,
            Supplier<List<ScheduledMessage>> messages
    ) {
        this.messageService = messageService;
        this.selector = selector;
        this.filter = filter;
        this.messages = messages;
    }

    public void dispatchNext(DispatchTarget target) {
        dispatchNext(target, true);
    }

    public void dispatchNext(
            DispatchTarget target,
            boolean advanceSelectorIndex
    ) {
        final List<ScheduledMessage> messages = this.messages.get();
        if (messages.isEmpty()) {
            return;
        }

        selector.selectNext(messages, advanceSelectorIndex)
                .ifPresent(message -> dispatch(message, target));
    }

    public void dispatch(
            ScheduledMessage message,
            DispatchTarget target
    ) {
        for (final Player player : target.recipients()) {
            if (filter.allows(player, message)) {
                sendToPlayer(player, message);
            }
        }
    }

    private void sendToPlayer(
            Player player,
            ScheduledMessage message
    ) {
        for (final Notice notice : message.notices()) {
            messageService.create()
                    .viewer(player)
                    .notice(notice)
                    .sendAsync();
        }
    }
}
