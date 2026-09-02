package com.github.imdmk.automessage.notice;

import com.github.imdmk.automessage.notice.time.DurationFormatter;
import com.github.imdmk.automessage.notice.time.DurationParser;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class NoticeSerializer implements ObjectSerializer<Notice> {

    private static final String TIMES_SEPARATOR = " ";
    private static final int TITLE_TIMES_COUNT = 3;

    @Override
    public boolean supports(@NotNull Class<? super Notice> type) {
        return Notice.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(Notice notice, SerializationData data, @NotNull GenericsDeclaration generics) {
        // A notice that is nothing but chat is written without any key at all - one line as a
        // bare string, several as a list. That is the entry an administrator writes most often.
        final ChatPart onlyChat = onlyChatOf(notice);

        if (onlyChat != null) {
            if (onlyChat.lines().size() == 1) {
                data.setValue(onlyChat.lines().getFirst(), String.class);
            } else {
                data.setValueCollection(onlyChat.lines(), String.class);
            }
            return;
        }

        for (final NoticePart part : notice.parts()) {
            write(part, data);
        }
    }

    private void write(NoticePart part, SerializationData data) {
        switch (part) {
            case ChatPart chat -> data.addCollection(ChatKey.MIXED, chat.lines(), String.class);
            case ActionBarPart actionBar -> data.add(ActionBarPart.KEY, actionBar.text(), String.class);
            case TitlePart title -> data.add(TitlePart.KEY, title.text(), String.class);
            case SubtitlePart subtitle -> data.add(SubtitlePart.KEY, subtitle.text(), String.class);
            case HideTitlePart ignored -> data.add(HideTitlePart.KEY, true, Boolean.class);
            case TitleTimesPart times -> data.add(TitleTimesPart.KEY, formatTimes(times), String.class);
            case BossBarPart bossBar -> writeBossBar(bossBar, data);
            case SoundPart sound -> data.add(SoundPart.KEY, formatSound(sound), String.class);
        }
    }

    private void writeBossBar(BossBarPart bossBar, SerializationData data) {
        data.addAsMap(BossBarPart.KEY, bossBarFields(bossBar), String.class, Object.class);
    }

    private java.util.Map<String, Object> bossBarFields(BossBarPart bossBar) {
        final java.util.LinkedHashMap<String, Object> fields = new java.util.LinkedHashMap<>();

        fields.put("message", bossBar.message());
        fields.put("duration", DurationFormatter.format(bossBar.duration()));
        fields.put("color", bossBar.color().name());
        fields.put("overlay", bossBar.overlay().name());

        // Left out when unset: a bar written purely as a banner is full, and a progress line
        // that always reads 1.0 is noise in every entry that uses one.
        if (bossBar.progress() != null) {
            fields.put("progress", String.valueOf(bossBar.progress()));
        }

        return fields;
    }

    private static String formatTimes(TitleTimesPart times) {
        return DurationFormatter.format(times.fadeIn())
                + TIMES_SEPARATOR + DurationFormatter.format(times.stay())
                + TIMES_SEPARATOR + DurationFormatter.format(times.fadeOut());
    }

    private static String formatSound(SoundPart sound) {
        if (sound.isBare()) {
            return keyOf(sound.sound());
        }

        return keyOf(sound.sound())
                + " " + sound.sourceOrDefault().name()
                + " " + sound.volumeOrDefault()
                + " " + sound.pitchOrDefault();
    }

    @Override
    public Notice deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        // A bare value is chat: either one line or a list of them.
        if (data.isValue()) {
            final Object raw = data.getValueRaw();

            if (raw instanceof Iterable<?>) {
                return Notice.of(new ChatPart(data.getValueAsList(String.class)));
            }

            return Notice.of(ChatPart.of(data.getValue(String.class)));
        }

        final List<NoticePart> parts = new ArrayList<>();

        if (data.containsKey(ChatKey.MIXED)) {
            parts.add(new ChatPart(data.getAsList(ChatKey.MIXED, String.class)));
        }

        if (data.containsKey(ActionBarPart.KEY)) {
            parts.add(new ActionBarPart(data.get(ActionBarPart.KEY, String.class)));
        }

        if (data.containsKey(TitlePart.KEY)) {
            parts.add(new TitlePart(data.get(TitlePart.KEY, String.class)));
        }

        if (data.containsKey(SubtitlePart.KEY)) {
            parts.add(new SubtitlePart(data.get(SubtitlePart.KEY, String.class)));
        }

        if (data.containsKey(TitleTimesPart.KEY)) {
            parts.add(parseTimes(data.get(TitleTimesPart.KEY, String.class)));
        }

        if (data.containsKey(HideTitlePart.KEY)) {
            parts.add(new HideTitlePart());
        }

        if (data.containsKey(BossBarPart.KEY)) {
            parts.add(readBossBar(data));
        }

        if (data.containsKey(SoundPart.KEY)) {
            parts.add(parseSound(data.get(SoundPart.KEY, String.class)));
        }

        if (parts.isEmpty()) {
            throw new IllegalArgumentException(
                    "a notice must say something: write a line of chat, or one of "
                            + "actionbar / title / subtitle / bossbar / sound"
            );
        }

        return new Notice(parts);
    }

    private BossBarPart readBossBar(DeserializationData data) {
        // okaeri hands a nested map back raw, so it is read as one rather than through a
        // second serializer that would only exist to unwrap four fields.
        @SuppressWarnings("unchecked")
        final java.util.Map<String, Object> fields =
                (java.util.Map<String, Object>) data.getRaw(BossBarPart.KEY);

        final Object progress = fields.get("progress");

        return new BossBarPart(
                String.valueOf(fields.get("message")),
                fields.containsKey("duration")
                        ? DurationParser.parse(String.valueOf(fields.get("duration")))
                        : BossBarPart.DEFAULT_DURATION,
                fields.containsKey("color")
                        ? BossBar.Color.valueOf(String.valueOf(fields.get("color")).toUpperCase(Locale.ROOT))
                        : BossBarPart.DEFAULT_COLOR,
                fields.containsKey("overlay")
                        ? BossBar.Overlay.valueOf(String.valueOf(fields.get("overlay")).toUpperCase(Locale.ROOT))
                        : BossBarPart.DEFAULT_OVERLAY,
                progress == null ? null : Double.valueOf(String.valueOf(progress))
        );
    }

    private static TitleTimesPart parseTimes(String value) {
        final String[] parts = value.trim().split("\\s+");

        if (parts.length != TITLE_TIMES_COUNT) {
            throw new IllegalArgumentException(
                    "title 'times' needs three durations - fade in, stay, fade out - but got: " + value
            );
        }

        return new TitleTimesPart(
                DurationParser.parse(parts[0]),
                DurationParser.parse(parts[1]),
                DurationParser.parse(parts[2])
        );
    }

    private static SoundPart parseSound(String value) {
        final String[] parts = value.trim().split("\\s+");

        final Key sound = Key.key(parts[0]);

        if (parts.length == 1) {
            return SoundPart.of(sound);
        }

        if (parts.length != 4) {
            throw new IllegalArgumentException(
                    "a sound is written as \"key\" or \"key source volume pitch\", but got: " + value
            );
        }

        return new SoundPart(
                sound,
                Sound.Source.valueOf(parts[1].toUpperCase(Locale.ROOT)),
                Float.valueOf(parts[2]),
                Float.valueOf(parts[3])
        );
    }

    private static String keyOf(Key key) {
        return Key.MINECRAFT_NAMESPACE.equals(key.namespace()) ? key.value() : key.asString();
    }

    private static ChatPart onlyChatOf(Notice notice) {
        if (notice.parts().size() != 1) {
            return null;
        }

        return notice.parts().getFirst() instanceof ChatPart chat ? chat : null;
    }

    private static final class ChatKey {
        private static final String MIXED = "chat";
    }
}
