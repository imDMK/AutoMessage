package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.locale.MessageTranslation;
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
    public void serialize(ScheduledMessage notice, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("name", notice.name(), String.class);
        data.addCollection("notices", notice.notices(), Notice.class);
        // Most messages go to everyone, and an empty "rules: []" on each of them is a line of
        // noise in a file people read top to bottom.
        if (!notice.rules().isEmpty()) {
            data.addCollection("rules", notice.rules(), AudienceRule.class);
        }

        // Each is only written when it differs from the default, so existing files stay
        // untouched and the common case does not grow lines of noise per message.
        if (notice.weight() != ScheduledMessage.DEFAULT_WEIGHT) {
            data.add("weight", notice.weight(), Integer.class);
        }

        if (!AnnouncementChannel.DEFAULT_NAME.equals(notice.channel())) {
            data.add("channel", notice.channel(), String.class);
        }

        if (notice.trigger() != null) {
            data.add("trigger", notice.trigger(), MessageTrigger.class);
        }

        if (!notice.translations().isEmpty()) {
            data.addCollection("translations", notice.translations(), MessageTranslation.class);
        }
    }

    @Override
    public ScheduledMessage deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        String name = data.get("name", String.class);
        List<Notice> notices =  data.getAsList("notices", Notice.class);
        final List<AudienceRule> rules = data.containsKey("rules")
                ? data.getAsList("rules", AudienceRule.class)
                : List.of();

        // Absent in every file written before these fields existed; such messages all weigh
        // the same, belong to the default channel and rotate rather than being triggered.
        final int weight = data.containsKey("weight")
                ? data.get("weight", Integer.class)
                : ScheduledMessage.DEFAULT_WEIGHT;

        final String channel = data.containsKey("channel")
                ? data.get("channel", String.class)
                : AnnouncementChannel.DEFAULT_NAME;

        final MessageTrigger trigger = data.containsKey("trigger")
                ? data.get("trigger", MessageTrigger.class)
                : null;

        final List<MessageTranslation> translations = data.containsKey("translations")
                ? data.getAsList("translations", MessageTranslation.class)
                : List.of();

        return new ScheduledMessage(
                name,
                notices,
                rules,
                weight,
                channel,
                trigger,
                translations
        );
    }
}
