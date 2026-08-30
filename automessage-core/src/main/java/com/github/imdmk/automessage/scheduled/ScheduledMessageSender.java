package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.NoticeBroadcast;
import com.github.imdmk.automessage.message.MessageConfig;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Turns a {@link ScheduledMessage} into the notices a single player actually receives.
 *
 * <p>
 * This is the one place where a scheduled message becomes chat, actionbar, title, bossbar or
 * sound output, so automatic broadcasts and manual previews always render identically -
 * placeholders included.
 * </p>
 */
public final class ScheduledMessageSender {

    private final Server server;
    private final MessageService messageService;
    private final ExternalPlaceholderResolver externalPlaceholderResolver;

    public ScheduledMessageSender(
            Server server,
            MessageService messageService,
            ExternalPlaceholderResolver externalPlaceholderResolver
    ) {
        this.server = server;
        this.messageService = messageService;
        this.externalPlaceholderResolver = externalPlaceholderResolver;
    }

    /**
     * Scans the message once so a broadcast does not repeat the work for every player.
     */
    public MessagePlaceholders placeholdersOf(ScheduledMessage message) {
        return MessagePlaceholders.scan(message, externalPlaceholderResolver);
    }

    /**
     * Sends every notice of the message on the calling thread.
     *
     * @param viewer  player receiving the message
     * @param message message to send
     */
    public void send(Player viewer, ScheduledMessage message) {
        send(viewer, message, placeholdersOf(message));
    }

    public void send(
            Player viewer,
            ScheduledMessage message,
            MessagePlaceholders placeholders
    ) {
        final Map<String, String> resolved = placeholders.resolveFor(server, viewer);

        for (final Notice notice : message.notices()) {
            broadcast(viewer, notice, resolved).send();
        }
    }

    /**
     * Sends every notice of the message off the calling thread.
     *
     * <p>
     * Placeholder values are resolved before the hand-off, on the caller's thread, because the
     * server state they read is only safe to touch there.
     * </p>
     *
     * @param viewer  player receiving the message
     * @param message message to send
     */
    public void sendAsync(Player viewer, ScheduledMessage message) {
        sendAsync(viewer, message, placeholdersOf(message));
    }

    public void sendAsync(
            Player viewer,
            ScheduledMessage message,
            MessagePlaceholders placeholders
    ) {
        final Map<String, String> resolved = placeholders.resolveFor(server, viewer);

        for (final Notice notice : message.notices()) {
            broadcast(viewer, notice, resolved).sendAsync();
        }
    }

    private NoticeBroadcast<CommandSender, MessageConfig, ?> broadcast(
            Player viewer,
            Notice notice,
            Map<String, String> placeholders
    ) {
        NoticeBroadcast<CommandSender, MessageConfig, ?> broadcast = messageService.create()
                .viewer(viewer)
                .notice(notice);

        for (final Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            broadcast = broadcast.placeholder(placeholder.getKey(), placeholder.getValue());
        }

        return broadcast;
    }
}
