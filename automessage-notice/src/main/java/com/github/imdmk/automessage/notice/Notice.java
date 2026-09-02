package com.github.imdmk.automessage.notice;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public record Notice(@Unmodifiable List<NoticePart> parts) {

    public Notice {
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("a notice needs at least one part");
        }

        parts = List.copyOf(parts);
    }

    public static Notice of(NoticePart... parts) {
        return new Notice(List.of(parts));
    }

    public static Notice chat(String... lines) {
        return of(ChatPart.of(lines));
    }

    public static Notice actionBar(String text) {
        return of(new ActionBarPart(text));
    }

    public static Notice title(String title) {
        return of(new TitlePart(title));
    }

    public static Notice subtitle(String subtitle) {
        return of(new SubtitlePart(subtitle));
    }

    public static Notice title(String title, String subtitle) {
        return of(new TitlePart(title), new SubtitlePart(subtitle));
    }

    public static Notice title(
            String title,
            String subtitle,
            Duration fadeIn,
            Duration stay,
            Duration fadeOut
    ) {
        return of(
                new TitlePart(title),
                new SubtitlePart(subtitle),
                new TitleTimesPart(fadeIn, stay, fadeOut)
        );
    }

    public static Notice hideTitle() {
        return of(new HideTitlePart());
    }

    public static Notice bossBar(
            BossBar.Color color,
            BossBar.Overlay overlay,
            Duration duration,
            String message
    ) {
        return of(new BossBarPart(message, duration, color, overlay, null));
    }

    public static Notice bossBar(
            BossBar.Color color,
            BossBar.Overlay overlay,
            Duration duration,
            double progress,
            String message
    ) {
        return of(new BossBarPart(message, duration, color, overlay, progress));
    }

    public static Notice sound(Key sound) {
        return of(SoundPart.of(sound));
    }

    public static Notice sound(Key sound, Sound.Source source, float volume, float pitch) {
        return of(new SoundPart(sound, source, volume, pitch));
    }

    @Unmodifiable
    public List<String> texts() {
        final List<String> texts = new ArrayList<>();

        for (final NoticePart part : parts) {
            switch (part) {
                case ChatPart chat -> texts.addAll(chat.lines());
                case ActionBarPart actionBar -> texts.add(actionBar.text());
                case TitlePart title -> texts.add(title.text());
                case SubtitlePart subtitle -> texts.add(subtitle.text());
                case BossBarPart bossBar -> texts.add(bossBar.message());
                case TitleTimesPart ignored -> { }
                case HideTitlePart ignored -> { }
                case SoundPart ignored -> { }
            }
        }

        return List.copyOf(texts);
    }

    @Unmodifiable
    public List<String> chatTexts() {
        final List<String> texts = new ArrayList<>();

        for (final NoticePart part : parts) {
            if (part instanceof ChatPart chat) {
                texts.addAll(chat.lines());
            }
        }

        return List.copyOf(texts);
    }
}
