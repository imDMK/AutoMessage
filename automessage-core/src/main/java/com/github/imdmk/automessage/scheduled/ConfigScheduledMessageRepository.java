package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;

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
    public List<ScheduledMessage> findScheduled() {
        return findAll().stream()
                .filter(ScheduledMessage::isScheduled)
                .toList();
    }

    @Override
    public List<ScheduledMessage> findByChannel(AnnouncementChannel channel) {
        // Scheduled only: a triggered message belongs to its event, not to a channel's rotation.
        return findScheduled().stream()
                .filter(message -> message.belongsTo(channel))
                .toList();
    }

    @Override
    public List<ScheduledMessage> findByTrigger(MessageTrigger.Type type) {
        return findAll().stream()
                .filter(message -> message.trigger() != null && message.trigger().type() == type)
                .toList();
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
