package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.language.LanguageRegistry;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;

import java.util.List;
import java.util.Objects;
import java.util.Map;

public final class ScheduledMessageSender {

    private final ViewerRegistry viewers;
    private final PluginLogger logger;
    private final MessageService messageService;
    private final LanguageRegistry languages;
    private final ExternalPlaceholderResolver externalPlaceholderResolver;

    public ScheduledMessageSender(
            ViewerRegistry viewers,
            PluginLogger logger,
            MessageService messageService,
            LanguageRegistry languages,
            ExternalPlaceholderResolver externalPlaceholderResolver
    ) {
        this.viewers = viewers;
        this.logger = logger;
        this.messageService = messageService;
        this.languages = languages;
        this.externalPlaceholderResolver = externalPlaceholderResolver;
    }

    public MessagePlaceholders placeholdersOf(ScheduledMessage message) {
        return MessagePlaceholders.scan(textOf(message), externalPlaceholderResolver);
    }

    private List<List<Notice>> textOf(ScheduledMessage message) {
        return languages.all().stream()
                .map(language -> language.announcement(message.name()))
                .filter(Objects::nonNull)
                .toList();
    }

    public void send(Viewer viewer, ScheduledMessage message) {
        send(viewer, message, placeholdersOf(message));
    }

    public void send(
            Viewer viewer,
            ScheduledMessage message,
            MessagePlaceholders placeholders
    ) {
        final List<Notice> notices = languages.announcement(message.name(), viewer.locale());

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

        final Map<String, String> resolved = placeholders.resolveFor(viewers, viewer);

        for (final Notice notice : notices) {
            var broadcast = messageService.create().viewer(viewer).notice(notice);

            for (final Map.Entry<String, String> placeholder : resolved.entrySet()) {
                broadcast = broadcast.placeholder(placeholder.getKey(), placeholder.getValue());
            }

            broadcast.send();
        }
    }
}
