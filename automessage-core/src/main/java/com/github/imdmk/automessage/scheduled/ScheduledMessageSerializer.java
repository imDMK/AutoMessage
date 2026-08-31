package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ScheduledMessageSerializer implements ObjectSerializer<ScheduledMessage> {

    @Override
    public boolean supports(@NotNull Class<? super ScheduledMessage> type) {
        return ScheduledMessage.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(ScheduledMessage message, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("name", message.name(), String.class);

        // Each is only written when it differs from the default, so the common case stays a
        // two-line entry and the file reads top to bottom.
        if (!message.rules().isEmpty()) {
            data.addCollection("rules", message.rules(), AudienceRule.class);
        }

        if (message.weight() != ScheduledMessage.DEFAULT_WEIGHT) {
            data.add("weight", message.weight(), Integer.class);
        }

        if (!AnnouncementChannel.DEFAULT_NAME.equals(message.channel())) {
            data.add("channel", message.channel(), String.class);
        }

        if (message.trigger() != null) {
            data.add("trigger", message.trigger(), MessageTrigger.class);
        }
    }

    @Override
    public ScheduledMessage deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        final String name = data.get("name", String.class);

        final List<AudienceRule> rules = data.containsKey("rules")
                ? data.getAsList("rules", AudienceRule.class)
                : List.of();

        final int weight = data.containsKey("weight")
                ? data.get("weight", Integer.class)
                : ScheduledMessage.DEFAULT_WEIGHT;

        final String channel = data.containsKey("channel")
                ? data.get("channel", String.class)
                : AnnouncementChannel.DEFAULT_NAME;

        final MessageTrigger trigger = data.containsKey("trigger")
                ? data.get("trigger", MessageTrigger.class)
                : null;

        return new ScheduledMessage(name, rules, weight, channel, trigger);
    }
}
