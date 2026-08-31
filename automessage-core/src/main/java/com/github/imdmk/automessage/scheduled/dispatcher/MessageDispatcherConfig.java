package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannelSerializer;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.duration.DurationFormat;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Header({
        "# ============================================================================",
        "#                        AutoMessage - messagesDispatcher.yml",
        "# ============================================================================",
        "# When announcements go out. What they say lives in scheduledMessages.yml.",
        "#",
        "# Out of the box there is one stream, named 'default', configured by the",
        "# settings below. Add entries under 'channels' when different announcements",
        "# need different intervals - tips every five minutes, shop adverts every",
        "# twenty.",
        "#",
        "# Time format used throughout this file:",
        "#   500ms   milliseconds        5m      minutes",
        "#   30s     seconds             1h      hours",
        "#   1m30s   units combine       10      a plain number means seconds",
        "#",
        "# Changes are applied by /automessage reload - no restart required.",
        "#",
        "# Source Code:",
        "#   https://github.com/imDMK/AutoMessage",
        "#",
        "# Support development:",
        "#   GitHub Sponsors: https://github.com/sponsors/imDMK",
        "#   PayPal:          https://paypal.me/dominiksuliga",
        "#",
        "# ============================================================================"
})
public final class MessageDispatcherConfig extends ConfigSection {

    @Comment({"#", "# Whether automatic scheduled-message dispatching is enabled.", "#"})
    public boolean enabled = true;

    @Comment({
            "#",
            "# How much time passes between two announcements.",
            "#",
            "# Accepted units: ms (milliseconds), s (seconds), m (minutes), h (hours).",
            "# Examples: 500ms, 10s, 5m, 1h, 1m30s",
            "# A plain number is read as seconds, so 'period: 10' means 10 seconds.",
            "#",
            "# The lowest value the server can schedule is 50ms (one tick), but anything",
            "# under a minute reads as spam to players. Five minutes is a comfortable",
            "# starting point; busy servers often go higher still.",
            "#"
    })
    @DurationSpec(fallbackUnit = ChronoUnit.SECONDS, format = DurationFormat.SIMPLIFIED)
    public Duration period = Duration.ofMinutes(5);

    @Comment({
            "#",
            "# How long to wait after the server starts before the first announcement.",
            "#",
            "# Uses the same time format as 'period', e.g. 10s, 1m, 500ms.",
            "# A plain number is read as seconds. Use 0s to announce immediately.",
            "#",
            "# The default gives players a minute to settle in after a restart before",
            "# the first announcement arrives.",
            "#"
    })
    @DurationSpec(fallbackUnit = ChronoUnit.SECONDS, format = DurationFormat.SIMPLIFIED)
    public Duration initialDelay = Duration.ofMinutes(1);

    @Comment({
            "#",
            "# Strategy used to select which scheduled message will be dispatched next.",
            "# Available options: SEQUENTIAL, SHUFFLE, RANDOM, WEIGHTED",
            "#",
            "#   SEQUENTIAL – cycle through the messages in the order they are written.",
            "#   SHUFFLE    – random order, but every message is shown once before any of",
            "#                them repeats. Usually what you want instead of RANDOM.",
            "#   RANDOM     – draw independently every time. The same message can come up",
            "#                twice or three times in a row.",
            "#   WEIGHTED   – draw at random, biased by the 'weight' of each message in",
            "#                scheduledMessages.yml. A message of weight 5 appears five",
            "#                times as often as one of weight 1.",
            "#"
    })
    public MessageSelectorType selector = MessageSelectorType.SHUFFLE;

    @Comment({
            "#",
            "# Additional announcement streams, each with its own schedule and rotation.",
            "#",
            "# The 'period', 'initialDelay' and 'selector' above configure the channel named",
            "# 'default', which is where a message lands when it does not name one. Everything",
            "# listed here runs alongside it, independently.",
            "#",
            "# A message joins a channel with 'channel: <name>' in scheduledMessages.yml.",
            "# Names are matched ignoring case. Messages naming a channel that does not exist",
            "# are reported on startup and never sent.",
            "#",
            "# Example - shop adverts every fifteen minutes, shuffled, running alongside",
            "# the default tips stream:",
            "#",
            "#   channels:",
            "#     - name: ads",
            "#       enabled: true",
            "#       initialDelay: 1m",
            "#       period: 15m",
            "#       selector: SHUFFLE",
            "#"
    })
    public List<AnnouncementChannel> channels = List.of();

    /**
     * Every channel that should be scheduled, the implicit default one first.
     *
     * <p>
     * The top-level timing fields predate channels and are still what most installations use, so
     * they keep working as the default channel's settings rather than being migrated into the
     * list. That way an existing messagesDispatcher.yml needs no changes at all.
     * </p>
     */
    public List<AnnouncementChannel> channels() {
        final List<AnnouncementChannel> resolved = new ArrayList<>();

        resolved.add(new AnnouncementChannel(
                AnnouncementChannel.DEFAULT_NAME,
                true,
                initialDelay == null ? Duration.ZERO : initialDelay,
                period == null ? DispatchTiming.DEFAULT_PERIOD : period,
                selector == null ? MessageSelectorType.SEQUENTIAL : selector
        ));

        for (final AnnouncementChannel channel : channels) {
            // A channel literally named "default" would otherwise shadow the one above and get
            // scheduled twice, sending every default-channel message in duplicate.
            if (!channel.isDefault()) {
                resolved.add(channel);
            }
        }

        return List.copyOf(resolved);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> registry.register(new AnnouncementChannelSerializer());
    }

    @Override
    public String getFileName() {
        return "messagesDispatcher.yml";
    }
}
