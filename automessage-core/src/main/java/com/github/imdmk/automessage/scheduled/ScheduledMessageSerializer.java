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
    }

    @Override
    public ScheduledMessage deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        String name = data.get("name", String.class);
        List<Notice> notices =  data.getAsList("notices", Notice.class);
        List<AudienceRule> rules = data.getAsList("rules", AudienceRule.class);

        return new ScheduledMessage(
                name,
                notices,
                rules
        );
    }
}
