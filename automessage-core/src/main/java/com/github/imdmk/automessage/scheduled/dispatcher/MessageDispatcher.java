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

/**
 * Coordinates selecting and dispatching scheduled messages to an audience.
 *
 * <p>This class is stateless and thread-safe under the assumption that:
 * <ul>
 *   <li>{@link MessageSelector} is thread-safe or externally synchronized.</li>
 *   <li>{@link Supplier} provides a thread-safe messages source.</li>
 * </ul>
 * </p>
 */
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
        this.messagesSupplier = Validator.notNull(messagesSupplier, "messagesSupplier");
    }

    /**
     * Selects the next scheduled message and dispatches it to the target audience.
     * The selector index is automatically advanced.
     */
    public void dispatchNext(@NotNull DispatchTarget target) {
        dispatchNext(target, true);
    }

    /**
     * Selects the next scheduled message and dispatches it to the provided audience.
     *
     * @param target the recipients wrapper
     * @param advanceSelectorIndex whether the selector should advance its internal index
     */
    public void dispatchNext(
            @NotNull DispatchTarget target,
            boolean advanceSelectorIndex
    ) {
        final List<ScheduledMessage> messages = messagesSupplier.get();
        if (messages.isEmpty()) {
            return;
        }

        final Optional<ScheduledMessage> next = selector.selectNext(messages, advanceSelectorIndex);
        next.ifPresent(message -> dispatch(message, target));
    }

    /**
     * Dispatches a specific message to all players permitted by the audience filter.
     *
     * @param message the message to send
     * @param target the target audience
     */
    public void dispatch(
            @NotNull ScheduledMessage message,
            @NotNull DispatchTarget target
    ) {
        for (final Player player : target.recipients()) {
            if (filter.allows(player, message)) {
                sendToPlayer(player, message);
            }
        }
    }

    /**
     * Sends all message notices to a single player asynchronously.
     */
    private void sendToPlayer(
            @NotNull Player player,
            @NotNull ScheduledMessage message
    ) {
        for (final Notice notice : message.notices()) {
            messageService.create()
                    .viewer(player)
                    .notice(notice)
                    .sendAsync();
        }
    }
}
