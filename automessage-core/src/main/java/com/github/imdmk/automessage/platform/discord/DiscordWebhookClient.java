package com.github.imdmk.automessage.platform.discord;

import com.github.imdmk.automessage.platform.logger.PluginLogger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public final class DiscordWebhookClient {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private static final long DEFAULT_BACKOFF_MILLIS = 60_000L;

    private static final int TOO_MANY_REQUESTS = 429;

    private final PluginLogger logger;
    private final HttpClient httpClient;
    private final URI endpoint;

    private final AtomicLong retryAfter = new AtomicLong(0L);

    public DiscordWebhookClient(PluginLogger logger, URI endpoint) {
        this.logger = logger;
        this.endpoint = endpoint;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    public void post(String payload, long now) {
        if (now < retryAfter.get()) {
            return;
        }

        final HttpRequest request = HttpRequest.newBuilder(endpoint)
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .header("User-Agent", "AutoMessage")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> handle(response.statusCode(), response, now))
                .exceptionally(throwable -> {
                    logger.warn(throwable, "Could not deliver an announcement to the Discord webhook.");
                    return null;
                });
    }

    private void handle(int statusCode, HttpResponse<Void> response, long now) {
        if (statusCode == TOO_MANY_REQUESTS) {
            final long backoff = response.headers()
                    .firstValue("Retry-After")
                    .map(DiscordWebhookClient::parseSeconds)
                    .orElse(DEFAULT_BACKOFF_MILLIS);

            // Never backwards: two responses can land out of order, and the later-expiring
            // window is the one Discord actually asked for.
            retryAfter.accumulateAndGet(now + backoff, Math::max);

            logger.warn(
                    "Discord asked AutoMessage to slow down; mirroring is paused for %d seconds.",
                    backoff / 1000L
            );
            return;
        }

        // 2xx is success; anything else is a configuration problem worth reporting once it happens.
        if (statusCode < 200 || statusCode >= 300) {
            logger.warn("Discord rejected an announcement with HTTP %d.", statusCode);
        }
    }

    public void close() {
        httpClient.shutdownNow();
    }

    private static long parseSeconds(String value) {
        try {
            return (long) (Double.parseDouble(value) * 1000.0D);
        } catch (NumberFormatException e) {
            return DEFAULT_BACKOFF_MILLIS;
        }
    }
}
