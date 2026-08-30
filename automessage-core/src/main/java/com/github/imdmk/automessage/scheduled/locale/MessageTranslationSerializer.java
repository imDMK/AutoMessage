package com.github.imdmk.automessage.scheduled.locale;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

public final class MessageTranslationSerializer implements ObjectSerializer<MessageTranslation> {

    @Override
    public boolean supports(@NotNull Class<? super MessageTranslation> type) {
        return MessageTranslation.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(
            MessageTranslation translation,
            SerializationData data,
            @NotNull GenericsDeclaration generics
    ) {
        data.add("locale", translation.locale(), String.class);
        data.addCollection("notices", translation.notices(), Notice.class);
    }

    @Override
    public MessageTranslation deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        return new MessageTranslation(
                data.get("locale", String.class),
                data.getAsList("notices", Notice.class)
        );
    }
}
