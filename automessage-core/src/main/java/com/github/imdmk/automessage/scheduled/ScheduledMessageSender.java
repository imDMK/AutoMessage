package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.NoticeBroadcast;
import com.github.imdmk.automessage.language.LanguageConfig;
import com.github.imdmk.automessage.language.LanguageRegistry;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Map;

/**
 * Turns a {@link ScheduledMessage} into the notices a single player actually receives.
 *
 * <p>
 * The message itself carries no text: scheduledMessages.yml says when a message is sent and to
 * whom, and the language files say what it says. This is where the two meet, in the language the
 * viewer's client is running.
 * </p>
 */
public final class ScheduledMessageSender {

    private final Server server;
    private final PluginLogger logger;
    private final MessageService messageService;
    private final LanguageRegistry languages;
    private final ExternalPlaceholderResolver externalPlaceholderResolver;

    public ScheduledMessageSender(
            Server server,
            PluginLogger logger,
            MessageService messageService,
            LanguageRegistry languages,
            ExternalPlaceholderResolver externalPlaceholderResolver
    ) {
        this.server = server;
        this.logger = logger;
        this.messageService = messageService;
        this.languages = languages;
        this.externalPlaceholderResolver = externalPlaceholderResolver;
    }

    /**
     * Scans the message once so a broadcast does not repeat the work for every player.
     *
     * <p>
     * Every language is scanned, not just the fallback: a placeholder used only in the Polish
     * text still has to be resolved for the players reading it.
     * </p>
     */
    public MessagePlaceholders placeholdersOf(ScheduledMessage message) {
        return MessagePlaceholders.scan(textOf(message), externalPlaceholderResolver);
    }

    private List<List<Notice>> textOf(ScheduledMessage message) {
        return languages.all().stream()
                .map(language -> language.announcement(message.name()))
                .filter(Objects::nonNull)
                .toList();
    }

    public void send(Player viewer, ScheduledMessage message) {
        send(viewer, message, placeholdersOf(message));
    }

    public void send(
            Player viewer,
            ScheduledMessage message,
            MessagePlaceholders placeholders
    ) {
        dispatch(viewer, message, placeholders, false);
    }

    /**
     * Sends every notice of the message off the calling thread.
     *
     * <p>
     * Placeholder values are resolved before the hand-off, on the caller's thread, because the
     * server state they read is only safe to touch there.
     * </p>
     */
    public void sendAsync(Player viewer, ScheduledMessage message) {
        sendAsync(viewer, message, placeholdersOf(message));
    }

    public void sendAsync(
            Player viewer,
            ScheduledMessage message,
            MessagePlaceholders placeholders
    ) {
        dispatch(viewer, message, placeholders, true);
    }

    private void dispatch(
            Player viewer,
            ScheduledMessage message,
            MessagePlaceholders placeholders,
            boolean async
    ) {
        final List<Notice> notices = languages.announcement(message.name(), viewer.getLocale());

        // A message named in scheduledMessages.yml with no text in any language cannot be sent.
        // Saying so once beats a player quietly receiving nothing.
        if (notices == null) {
            logger.warn(
                    "Message '%s' has no text in any language file - add it under 'announcements' in lang/%s.yml.",
                    message.name(),
                    languages.fallback().code()
            );
            return;
        }

        final Map<String, String> resolved = placeholders.resolveFor(server, viewer);

        for (final Notice notice : notices) {
            final NoticeBroadcast<CommandSender, LanguageConfig, ?> broadcast =
                    broadcast(viewer, notice, resolved);

            if (async) {
                broadcast.sendAsync();
            } else {
                broadcast.send();
            }
        }
    }

    private NoticeBroadcast<CommandSender, LanguageConfig, ?> broadcast(
            Player viewer,
            Notice notice,
            Map<String, String> placeholders
    ) {
        NoticeBroadcast<CommandSender, LanguageConfig, ?> broadcast = messageService.create()
                .viewer(viewer)
                .notice(notice);

        for (final Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            broadcast = broadcast.placeholder(placeholder.getKey(), placeholder.getValue());
        }

        return broadcast;
    }
}
