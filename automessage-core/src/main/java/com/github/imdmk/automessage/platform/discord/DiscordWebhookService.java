package com.github.imdmk.automessage.platform.discord;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.dispatcher.DispatchObserver;
import com.github.imdmk.automessage.scheduled.placeholder.MessagePlaceholders;
import org.bukkit.Server;

import java.net.URI;
import java.util.Optional;

/**
 * Mirrors each dispatched announcement to a Discord channel.
 *
 * <p>
 * Observes the dispatcher rather than sitting inside it, so a Discord outage cannot affect what
 * players on the server receive.
 * </p>
 */
public final class DiscordWebhookService implements DispatchObserver {

    private final Server server;
    private final DiscordWebhookConfig config;
    private final DiscordWebhookClient client;

    private DiscordWebhookService(
            Server server,
            DiscordWebhookConfig config,
            DiscordWebhookClient client
    ) {
        this.server = server;
        this.config = config;
        this.client = client;
    }

    /**
     * @return a service that posts, or {@link DispatchObserver#none()} when Discord mirroring is
     *         switched off or configured with a URL that is not a Discord webhook
     */
    public static DispatchObserver create(Server server, PluginLogger logger, DiscordWebhookConfig config) {
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
        return new DiscordWebhookService(server, config, new DiscordWebhookClient(logger, endpoint.get()));
    }

    @Override
    public void onDispatched(ScheduledMessage message, MessagePlaceholders placeholders) {
        final String content = DiscordMessageRenderer.render(
                message,
                placeholders.resolveWithoutViewer(server)
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
}
