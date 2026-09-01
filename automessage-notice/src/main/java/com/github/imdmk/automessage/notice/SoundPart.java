package com.github.imdmk.automessage.notice;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record SoundPart(
        Key sound,
        @Nullable Sound.Source source,
        @Nullable Float volume,
        @Nullable Float pitch
) implements NoticePart {

    public static final String KEY = "sound";

    public static final Sound.Source DEFAULT_SOURCE = Sound.Source.MASTER;
    public static final float DEFAULT_VOLUME = 1.0F;
    public static final float DEFAULT_PITCH = 1.0F;

    public SoundPart {
        Objects.requireNonNull(sound, "sound");
    }

    public static SoundPart of(Key sound) {
        return new SoundPart(sound, null, null, null);
    }

    public Sound.Source sourceOrDefault() {
        return source == null ? DEFAULT_SOURCE : source;
    }

    public float volumeOrDefault() {
        return volume == null ? DEFAULT_VOLUME : volume;
    }

    public float pitchOrDefault() {
        return pitch == null ? DEFAULT_PITCH : pitch;
    }

    public boolean isBare() {
        return source == null && volume == null && pitch == null;
    }

    @Override
    public String key() {
        return KEY;
    }
}
