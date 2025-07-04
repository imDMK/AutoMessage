package com.github.imdmk.automessage.feature.message.auto.sound;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class AutoMessageSerializer implements ObjectSerializer<AutoMessageSound> {

    @Override
    public boolean supports(@NotNull Class<? super AutoMessageSound> type) {
        return AutoMessageSound.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull AutoMessageSound autoSound, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("sound", autoSound.sound(), Sound.class);
        data.add("volume", autoSound.volume(), float.class);
        data.add("pitch", autoSound.pitch(), float.class);
    }

    @Override
    public AutoMessageSound deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        Sound sound = data.get("sound", Sound.class);
        float volume = data.get("volume", float.class);
        float pitch = data.get("pitch", float.class);

        return new AutoMessageSound(sound, volume, pitch);
    }
}
