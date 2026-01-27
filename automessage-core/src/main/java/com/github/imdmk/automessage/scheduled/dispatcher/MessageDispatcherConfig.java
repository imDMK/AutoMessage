package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

import java.time.Duration;

@Header({
        "# ============================================================================",
        "#                        AutoMessage — messagesDispatcher.yml",
        "# ============================================================================",
        "# This file controls how AutoMessage dispatches scheduled announcements.",
        "# It defines startup timing, repeat intervals, and which message-selection",
        "# strategy the dispatcher should use.",
        "#",
        "# Fields:",
        "#  enabled:",
        "#    Master switch controlling whether automatic message dispatching is active.",
        "#    true  -> messages cycle automatically",
        "#    false -> announcements are paused",
        "#",
        "#  period:",
        "#    Time between consecutive dispatch executions.",
        "#    Examples: 10s, 1m, 500ms",
        "#",
        "#  initialDelay:",
        "#    How long the dispatcher should wait before sending the first message",
        "#    after plugin startup.",
        "#",
        "#  selector:",
        "#    Strategy determining which scheduled message is selected next.",
        "#    Supported types:",
        "#       SEQUENTIAL – cycle through messages in order",
        "#       RANDOM     – choose a random message each time",
        "#",
        "# Notes:",
        "#  • This file works together with scheduledMessages.yml.",
        "#  • After editing this file you must restart the server, as dispatcher",
        "#    scheduling is configured during plugin initialization.",
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

    @Comment({"#", "# Delay between automatic dispatch executions.", "#"})
    public Duration period = Duration.ofSeconds(10);

    @Comment({"#", "# Initial delay before the very first automatic message dispatch.", "#"})
    public Duration initialDelay = Duration.ofSeconds(10);

    @Comment({
            "#",
            "# Strategy used to select which scheduled message will be dispatched next.",
            "# Available options: SEQUENTIAL, RANDOM",
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
