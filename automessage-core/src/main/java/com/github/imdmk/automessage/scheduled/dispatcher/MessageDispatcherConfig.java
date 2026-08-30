package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.duration.DurationFormat;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

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
        "#  selector:",
        "#    Strategy determining which scheduled message is selected next.",
        "#    Supported types:",
        "#       SEQUENTIAL – cycle through messages in order",
        "#       SHUFFLE    – random order, but every message is shown once before any repeats",
        "#       RANDOM     – choose a random message each time (may repeat back to back)",
        "#       WEIGHTED   – random, biased by each message's 'weight' field",
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
    public MessageSelectorType selector = MessageSelectorType.SEQUENTIAL;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public String getFileName() {
        return "messagesDispatcher.yml";
    }
}
