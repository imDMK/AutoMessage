package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.config.ConfigReloadListener;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

final class ConfigScheduledMessageRepository implements ScheduledMessageRepository, ConfigReloadListener {

    private final ScheduledMessagesConfig config;

    // Volatile rather than synchronized: read from the dispatch thread and replaced from the
    // async reload task, and a reader sees either the old index or the new one, never a half-built
    // one. The views are derived once because every join asks for three of them, on the main
    // thread.
    private volatile Index index;

    ConfigScheduledMessageRepository(ScheduledMessagesConfig config) {
        this.config = config;
    }

    @Override
    public List<ScheduledMessage> findAll() {
        return index().all();
    }

    @Override
    public List<ScheduledMessage> findScheduled() {
        return index().scheduled();
    }

    @Override
    public List<ScheduledMessage> findByChannel(AnnouncementChannel channel) {
        // Scheduled only: a triggered message belongs to its event, not to a channel's rotation.
        return index().scheduled().stream()
                .filter(message -> message.belongsTo(channel))
                .toList();
    }

    @Override
    public List<ScheduledMessage> findByTrigger(MessageTrigger.Type type) {
        return index().byTrigger().getOrDefault(type, List.of());
    }

    @Override
    public Optional<ScheduledMessage> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(index().byName().get(normalize(name)));
    }

    @Override
    public List<String> names() {
        return index().names();
    }

    @Override
    public void onConfigReload() {
        this.index = null;
    }

    // The list instance is compared as well as the cached index: a reload that bypasses
    // onConfigReload still replaces config.messages, and serving the previous index then would
    // announce messages the administrator has already deleted.
    private Index index() {
        final List<ScheduledMessage> messages = config.messages;
        final Index current = this.index;

        if (current != null && current.source() == messages) {
            return current;
        }

        final Index rebuilt = Index.of(messages);
        this.index = rebuilt;

        return rebuilt;
    }

    private static String normalize(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    private record Index(
            List<ScheduledMessage> source,
            List<ScheduledMessage> all,
            List<ScheduledMessage> scheduled,
            Map<MessageTrigger.Type, List<ScheduledMessage>> byTrigger,
            Map<String, ScheduledMessage> byName,
            List<String> names
    ) {

        static Index of(List<ScheduledMessage> messages) {
            final List<ScheduledMessage> all = messages == null ? List.of() : List.copyOf(messages);

            final Map<MessageTrigger.Type, List<ScheduledMessage>> byTrigger =
                    new EnumMap<>(MessageTrigger.Type.class);

            final Map<String, ScheduledMessage> byName = new java.util.HashMap<>();

            for (final ScheduledMessage message : all) {
                if (message.trigger() != null) {
                    byTrigger.computeIfAbsent(message.trigger().type(), key -> new java.util.ArrayList<>())
                            .add(message);
                }

                // First entry wins, matching the previous findFirst() behaviour when two messages
                // share a name.
                byName.putIfAbsent(normalize(message.name()), message);
            }

            byTrigger.replaceAll((type, list) -> List.copyOf(list));

            return new Index(
                    messages,
                    all,
                    all.stream().filter(ScheduledMessage::isScheduled).toList(),
                    Map.copyOf(byTrigger),
                    Map.copyOf(byName),
                    all.stream().map(ScheduledMessage::name).toList()
            );
        }
    }
}
