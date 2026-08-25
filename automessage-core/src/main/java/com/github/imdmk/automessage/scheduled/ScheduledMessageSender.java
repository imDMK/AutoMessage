package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.message.MessageService;
import org.bukkit.entity.Player;

/**
 * Turns a {@link ScheduledMessage} into the notices a single player actually receives.
 *
 * <p>
 * This is the one place where a scheduled message becomes chat, actionbar, title, bossbar or
 * sound output, so automatic broadcasts and manual previews always render identically.
 * </p>
 */
public final class ScheduledMessageSender {

    private final MessageService messageService;

    public ScheduledMessageSender(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Sends every notice of the message on the calling thread.
     *
     * @param viewer  player receiving the message
     * @param message message to send
     */
    public void send(Player viewer, ScheduledMessage message) {
        for (final Notice notice : message.notices()) {
            messageService.create()
                    .viewer(viewer)
                    .notice(notice)
                    .send();
        }
    }

    /**
     * Sends every notice of the message off the calling thread.
     *
     * @param viewer  player receiving the message
     * @param message message to send
     */
    public void sendAsync(Player viewer, ScheduledMessage message) {
        for (final Notice notice : message.notices()) {
            messageService.create()
                    .viewer(viewer)
                    .notice(notice)
                    .sendAsync();
        }
    }
}
