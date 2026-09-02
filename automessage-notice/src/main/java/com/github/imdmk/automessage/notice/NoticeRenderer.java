package com.github.imdmk.automessage.notice;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.title.Title;

import java.time.Duration;

import java.util.Objects;
import java.util.function.UnaryOperator;

public final class NoticeRenderer {

    private static final long MILLIS_PER_STEP = 100L;
    private static final int MAX_COUNTDOWN_STEPS = 40;

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

        // A bar with a duration and no progress of its own is a countdown, and reads as broken
        // when it sits full until it vanishes. One that names a progress was asked for that exact
        // fill, so it keeps it and simply disappears when its time is up.
        if (part.progress() != null || part.duration().isZero()) {
            delayer.runLater(part.duration(), () -> audience.hideBossBar(bossBar));
            return;
        }

        final int steps = countdownSteps(part.duration());
        drain(bossBar, audience, part.duration().dividedBy(steps), steps, 1);
    }

    /**
     * Each step is one packet to one player, so the count is capped rather than tied to the
     * duration: a minute-long bar drawn every tick would be three thousand updates per viewer.
     */
    private static int countdownSteps(Duration duration) {
        final long byRate = duration.toMillis() / MILLIS_PER_STEP;

        return (int) Math.max(1L, Math.min(MAX_COUNTDOWN_STEPS, byRate));
    }

    // Re-scheduled rather than repeated, because runLater is the only clock this module is given -
    // see NoticeDelayer, and the reason it is the only one.
    private void drain(BossBar bossBar, Audience audience, Duration step, int steps, int done) {
        delayer.runLater(step, () -> {
            // Set first, hide second: the last step is the one that reaches empty, and hiding
            // before it would leave the bar visibly short of zero for its whole final frame.
            bossBar.progress(Math.max(BossBar.MIN_PROGRESS, 1.0F - (float) done / steps));

            if (done >= steps) {
                audience.hideBossBar(bossBar);
                return;
            }

            drain(bossBar, audience, step, steps, done + 1);
        });
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
