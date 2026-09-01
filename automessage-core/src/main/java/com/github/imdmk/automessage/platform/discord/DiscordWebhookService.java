package com.github.imdmk.automessage.platform.discord;

import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.language.LanguageRegistry;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.dispatcher.DispatchObserver;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public final class DiscordWebhookService implements DispatchObserver {

    private final ViewerRegistry viewers;
    private final LanguageRegistry languages;
    private final DiscordWebhookConfig config;
    private final DiscordWebhookClient client;

    private DiscordWebhookService(
            ViewerRegistry viewers,
            LanguageRegistry languages,
            DiscordWebhookConfig config,
            DiscordWebhookClient client
    ) {
        this.viewers = viewers;
        this.languages = languages;
        this.config = config;
        this.client = client;
    }

    public static DispatchObserver create(
            ViewerRegistry viewers,
            LanguageRegistry languages,
            PluginLogger logger,
            DiscordWebhookConfig config
    ) {
        if (!config.enabled) {
            return DispatchObserver.none();
        }

        final Optional<URI> endpoint = DiscordWebhookUrl.parse(config.url);

        if (endpoint.isEmpty()) {
            logger.warn(
                    "Discord mirroring is enabled but 'url' in discordWebhook.yml is not a Discord "
                            + "webhook address - nothing will be sent."
            );
            return DispatchObserver.none();
        }

        logger.info("Discord mirroring is enabled.");
        return new DiscordWebhookService(viewers, languages, config, new DiscordWebhookClient(logger, endpoint.get()));
    }

    @Override
    public void onDispatched(ScheduledMessage message, MessagePlaceholders placeholders) {
        final List<Notice> notices = languages.fallback().announcement(message.name());

        if (notices == null) {
            return;
        }

        final String content = DiscordMessageRenderer.render(
                notices,
                placeholders.resolveWithoutViewer(viewers)
        );

        // A message made only of a title or a sound has nothing to show in a text channel.
        if (content.isBlank()) {
            return;
        }

        client.post(
                DiscordPayload.build(content, config.username, config.avatarUrl),
                System.currentTimeMillis()
        );
    }

    @Override
    public void shutdown() {
        client.close();
    }
}
