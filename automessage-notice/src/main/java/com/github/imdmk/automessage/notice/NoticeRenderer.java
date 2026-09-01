package com.github.imdmk.automessage.notice;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.title.Title;

import java.util.Objects;
import java.util.function.UnaryOperator;

public final class NoticeRenderer {

    private final ComponentSerializer<Component, Component, String> serializer;
    private final NoticeDelayer delayer;

    public NoticeRenderer(
            ComponentSerializer<Component, Component, String> serializer,
            NoticeDelayer delayer
    ) {
        this.serializer = Objects.requireNonNull(serializer, "serializer");
        this.delayer = Objects.requireNonNull(delayer, "delayer");
    }

    public static NoticeRenderer miniMessage(NoticeDelayer delayer) {
        return new NoticeRenderer(MiniMessage.miniMessage(), delayer);
    }

    public void render(Notice notice, Audience audience) {
        render(notice, audience, UnaryOperator.identity());
    }

    public void render(Notice notice, Audience audience, UnaryOperator<String> placeholders) {
        // Collected first: a title, its subtitle and its timing are separate parts of one entry
        // and Adventure wants them as a single Title.
        Component title = null;
        Component subtitle = null;
        Title.Times times = null;

        for (final NoticePart part : notice.parts()) {
            switch (part) {
                case ChatPart chat -> {
                    for (final String line : chat.lines()) {
                        audience.sendMessage(parse(line, placeholders));
                    }
                }
                case ActionBarPart actionBar -> audience.sendActionBar(parse(actionBar.text(), placeholders));
                case TitlePart part1 -> title = parse(part1.text(), placeholders);
                case SubtitlePart part1 -> subtitle = parse(part1.text(), placeholders);
                case TitleTimesPart part1 -> times = Title.Times.times(part1.fadeIn(), part1.stay(), part1.fadeOut());
                case HideTitlePart ignored -> audience.clearTitle();
                case BossBarPart bossBar -> showBossBar(bossBar, audience, placeholders);
                case SoundPart sound -> audience.playSound(toSound(sound));
            }
        }

        if (title != null || subtitle != null) {
            audience.showTitle(Title.title(
                    title == null ? Component.empty() : title,
                    subtitle == null ? Component.empty() : subtitle,
                    times == null ? Title.DEFAULT_TIMES : times
            ));
        }
    }

    private void showBossBar(BossBarPart part, Audience audience, UnaryOperator<String> placeholders) {
        final BossBar bossBar = BossBar.bossBar(
                parse(part.message(), placeholders),
                part.progressOrFull(),
                part.color(),
                part.overlay()
        );

        audience.showBossBar(bossBar);
        delayer.runLater(part.duration(), () -> audience.hideBossBar(bossBar));
    }

    private static Sound toSound(SoundPart part) {
        return Sound.sound(
                part.sound(),
                part.sourceOrDefault(),
                part.volumeOrDefault(),
                part.pitchOrDefault()
        );
    }

    private Component parse(String text, UnaryOperator<String> placeholders) {
        return serializer.deserialize(placeholders.apply(text));
    }
}
