package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.config.ConfigReloadListener;
import com.github.imdmk.automessage.language.LanguageConfig;
import com.github.imdmk.automessage.language.LanguageRegistry;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.message.TextSubstitution;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;
import net.kyori.adventure.audience.Audience;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

public final class ScheduledMessageSender implements ConfigReloadListener {

    private final ViewerRegistry viewers;
    private final PluginLogger logger;
    private final MessageService messageService;
    private final LanguageRegistry languages;
    private final ExternalPlaceholderResolver externalPlaceholderResolver;

    // Which placeholders a message contains is decided by the text, and the text only changes
    // when the files do - so the scan is kept until a reload rather than repeated on every
    // announcement, which walked every translation of every language each time.
    private final Map<String, MessagePlaceholders> scanned = new ConcurrentHashMap<>();

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

    @Override
    public void onConfigReload() {
        this.scanned.clear();
    }

    public MessagePlaceholders placeholdersOf(ScheduledMessage message) {
        return scanned.computeIfAbsent(
                message.name(),
                name -> MessagePlaceholders.scan(textOf(name), externalPlaceholderResolver)
        );
    }

    private List<List<Notice>> textOf(String messageName) {
        return languages.all().stream()
                .map(language -> language.announcement(messageName))
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

        if (notices == null) {
            warnAboutMissingText(message);
            return;
        }

        render(List.of(viewer.audience()), notices, placeholders.resolveFor(viewers, viewer));
    }

    /**
     * Sends one announcement to a whole audience at once.
     */
    public void sendAll(
            Collection<Viewer> recipients,
            ScheduledMessage message,
            MessagePlaceholders placeholders
    ) {
        if (recipients.isEmpty()) {
            return;
        }

        // A message that reads differently for each player has to be built for each of them.
        // Anything else is the same text for everyone sharing a language, so it is parsed once
        // and handed to all of them together.
        if (placeholders.viewerScoped()) {
            for (final Viewer viewer : recipients) {
                send(viewer, message, placeholders);
            }
            return;
        }

        final Map<String, String> resolved = placeholders.resolveWithoutViewer(viewers);
        boolean warned = false;

        for (final Map.Entry<LanguageConfig, List<Audience>> group : groupByLanguage(recipients).entrySet()) {
            final List<Notice> notices = languages.announcement(message.name(), group.getKey());

            if (notices == null) {
                if (!warned) {
                    warnAboutMissingText(message);
                    warned = true;
                }
                continue;
            }

            render(group.getValue(), notices, resolved);
        }
    }

    private Map<LanguageConfig, List<Audience>> groupByLanguage(Collection<Viewer> recipients) {
        // Keyed by the language itself rather than by its code: the registry hands out one
        // instance per language, and two codes that resolve to it belong in the same group.
        final Map<LanguageConfig, List<Audience>> byLanguage = new LinkedHashMap<>();

        for (final Viewer viewer : recipients) {
            byLanguage
                    .computeIfAbsent(languages.provide(viewer.locale()), language -> new ArrayList<>())
                    .add(viewer.audience());
        }

        return byLanguage;
    }

    private void render(List<Audience> audiences, List<Notice> notices, Map<String, String> placeholders) {
        final UnaryOperator<String> substitution = TextSubstitution.of(placeholders);

        for (final Notice notice : notices) {
            messageService.render(audiences, notice, substitution);
        }
    }

    // A message named in scheduledMessages.yml with no text in any language cannot be sent.
    // Saying so once beats a player quietly receiving nothing.
    private void warnAboutMissingText(ScheduledMessage message) {
        logger.warn(
                "Message '%s' has no text in any language file - add it under 'announcements' in lang/%s.yml.",
                message.name(),
                languages.fallback().code()
        );
    }
}
