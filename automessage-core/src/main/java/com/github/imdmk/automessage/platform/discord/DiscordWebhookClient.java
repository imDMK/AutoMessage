package com.github.imdmk.automessage.platform.discord;

import com.github.imdmk.automessage.platform.logger.PluginLogger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Posts a message body to a Discord webhook.
 *
 * <p>
 * Every request is asynchronous. The main thread must never wait on a network round trip, and even
 * the plugin's async pool should not sit blocked on a Discord outage.
 * </p>
 */
public final class DiscordWebhookClient {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    /** How long to stay quiet after Discord asks us to slow down, when it gives no figure. */
    private static final long DEFAULT_BACKOFF_MILLIS = 60_000L;

    private static final int TOO_MANY_REQUESTS = 429;

    private final PluginLogger logger;
    private final HttpClient httpClient;
    private final URI endpoint;

    /**
     * Wall-clock time before which no request is attempted.
     *
     * <p>
     * Discord answers a flood with 429 and a retry hint. Ignoring it earns a longer ban for the
     * webhook, so a rejected request silences the rest until the window passes.
     * </p>
     */
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

            retryAfter.set(now + backoff);

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

    private static long parseSeconds(String value) {
        try {
            return (long) (Double.parseDouble(value) * 1000.0D);
        } catch (NumberFormatException e) {
            return DEFAULT_BACKOFF_MILLIS;
        }
    }
}
