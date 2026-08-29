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
        "#                        AutoMessage — messagesDispatcher.yml",
        "# ============================================================================",
        "# This file controls how AutoMessage dispatches scheduled announcements.",
        "# It defines startup timing, repeat intervals, and which message-selection",
        "# strategy the dispatcher should use.",
        "#",
        "# Time format used by this file:",
        "#  Every time value is written as a number followed by a unit:",
        "#    ms -> milliseconds     e.g. 500ms",
        "#    s  -> seconds          e.g. 30s",
        "#    m  -> minutes          e.g. 5m",
        "#    h  -> hours            e.g. 1h",
        "#  Units can be combined, e.g. 1m30s.",
        "#  A plain number without a unit is read as SECONDS, so 'period: 10' equals 10s.",
        "#",
        "# Fields:",
        "#  enabled:",
        "#    Master switch controlling whether automatic message dispatching is active.",
        "#    true  -> messages cycle automatically",
        "#    false -> announcements are paused",
        "#",
        "#  period:",
        "#    How much time passes between two announcements.",
        "#    Examples: 10s (ten seconds), 5m (five minutes), 500ms, 1m30s",
        "#    Minimum value is 50ms (one server tick); lower values are raised to it.",
        "#",
        "#  initialDelay:",
        "#    How long the dispatcher waits before sending the first announcement",
        "#    after plugin startup. Uses the same time format as 'period'.",
        "#",
        "#  channels:",
        "#    Extra announcement streams that run alongside the default one, each with its",
        "#    own period, initialDelay, selector and on/off switch. See the comment on the",
        "#    field itself for the exact shape.",
        "#",
        "#  selector:",
        "#    Strategy determining which scheduled message is selected next.",
        "#    Supported types:",
        "#       SEQUENTIAL – cycle through messages in order",
        "#       RANDOM     – choose a random message each time",
        "#",
        "# Notes:",
        "#  • This file works together with scheduledMessages.yml.",
        "#  • Changes are applied by running /automessage reload — no restart required.",
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
            "# The lowest value the server can schedule is 50ms (one tick).",
            "#"
    })
    @DurationSpec(fallbackUnit = ChronoUnit.SECONDS, format = DurationFormat.SIMPLIFIED)
    public Duration period = Duration.ofSeconds(10);

    @Comment({
            "#",
            "# How long to wait after the server starts before the first announcement.",
            "#",
            "# Uses the same time format as 'period', e.g. 10s, 1m, 500ms.",
            "# A plain number is read as seconds. Use 0s to announce immediately.",
            "#"
    })
    @DurationSpec(fallbackUnit = ChronoUnit.SECONDS, format = DurationFormat.SIMPLIFIED)
    public Duration initialDelay = Duration.ofSeconds(10);

    @Comment({
            "#",
            "# Strategy used to select which scheduled message will be dispatched next.",
            "# Available options: SEQUENTIAL, RANDOM",
            "#"
    })
    public MessageSelectorType selector = MessageSelectorType.SEQUENTIAL;

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
            "# Example - shop adverts every fifteen minutes, shuffled, alongside the default",
            "# tips stream (SHUFFLE is available once that selector ships):",
            "#",
            "#   channels:",
            "#     - name: ads",
            "#       enabled: true",
            "#       initialDelay: 1m",
            "#       period: 15m",
            "#       selector: RANDOM",
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
