package com.github.imdmk.automessage.scheduled;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * {@link ScheduledMessageRepository} reading directly from {@link ScheduledMessagesConfig}.
 *
 * <p>
 * The message list is read on every call rather than cached, because reloading the configuration
 * replaces it.
 * </p>
 */
final class ConfigScheduledMessageRepository implements ScheduledMessageRepository {

    private final ScheduledMessagesConfig config;

    ConfigScheduledMessageRepository(ScheduledMessagesConfig config) {
        this.config = config;
    }

    @Override
    public List<ScheduledMessage> findAll() {
        final List<ScheduledMessage> messages = config.messages;
        return messages == null ? List.of() : List.copyOf(messages);
    }

    @Override
    public Optional<ScheduledMessage> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        final String normalized = normalize(name);

        return findAll().stream()
                .filter(message -> normalize(message.name()).equals(normalized))
                .findFirst();
    }

    @Override
    public List<String> names() {
        return findAll().stream()
                .map(ScheduledMessage::name)
                .toList();
    }

    private static String normalize(String name) {
        return name.toLowerCase(Locale.ROOT);
    }
}
