package com.github.imdmk.automessage.configuration.serializer;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;

public class TitleTimesSerializer implements ObjectSerializer<Title.Times> {

    @Override
    public boolean supports(@NonNull Class<? super Title.Times> type) {
        return Title.Times.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(Title.Times times, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("fadeIn", times.fadeIn(), Duration.class);
        data.add("stay", times.stay(), Duration.class);
        data.add("fadeOut", times.fadeOut(), Duration.class);
    }

    @Override
    public Title.Times deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        Duration fadeIn = data.get("fadeIn", Duration.class);
        Duration stay = data.get("stay", Duration.class);
        Duration fadeOut = data.get("fadeOut", Duration.class);

        return Title.Times.times(fadeIn, stay, fadeOut);
    }
}
