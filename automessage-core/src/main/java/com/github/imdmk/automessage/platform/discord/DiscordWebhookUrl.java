package com.github.imdmk.automessage.platform.discord;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;

public final class DiscordWebhookUrl {

    private static final String HTTPS = "https";

    private static final java.util.Set<String> ALLOWED_HOSTS = java.util.Set.of(
            "discord.com",
            "discordapp.com",
            "canary.discord.com",
            "ptb.discord.com"
    );

    private DiscordWebhookUrl() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static Optional<URI> parse(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }

        final URI uri;
        try {
            uri = URI.create(url.trim());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        if (!HTTPS.equalsIgnoreCase(uri.getScheme())) {
            return Optional.empty();
        }

        final String host = uri.getHost();
        if (host == null || !ALLOWED_HOSTS.contains(host.toLowerCase(Locale.ROOT))) {
            return Optional.empty();
        }

        final String path = uri.getPath();
        if (path == null || !path.startsWith("/api/webhooks/")) {
            return Optional.empty();
        }

        return Optional.of(uri);
    }
}
