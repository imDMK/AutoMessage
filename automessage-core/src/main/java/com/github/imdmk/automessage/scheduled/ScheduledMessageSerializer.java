package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
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
        data.addCollection("rules", notice.rules(), AudienceRule.class);

        // Only written when it differs from the default, so existing files stay untouched and
        // the common case does not grow a line of noise per message.
        if (notice.weight() != ScheduledMessage.DEFAULT_WEIGHT) {
            data.add("weight", notice.weight(), Integer.class);
        }
    }

    @Override
    public ScheduledMessage deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        String name = data.get("name", String.class);
        List<Notice> notices =  data.getAsList("notices", Notice.class);
        List<AudienceRule> rules = data.getAsList("rules", AudienceRule.class);

        // Absent in every file written before weights existed; those messages all weigh the same.
        int weight = data.containsKey("weight")
                ? data.get("weight", Integer.class)
                : ScheduledMessage.DEFAULT_WEIGHT;

        return new ScheduledMessage(
                name,
                notices,
                rules,
                weight
        );
    }
}
