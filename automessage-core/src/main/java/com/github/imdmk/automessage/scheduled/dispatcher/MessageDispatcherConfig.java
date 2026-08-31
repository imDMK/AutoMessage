package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannelSerializer;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

import java.time.Duration;
import java.util.List;

@Header({
        "# ============================================================================",
        "#                             AutoMessage - config.yml",
        "# ============================================================================",
        "# How often announcements go out, and in what language.",
        "#",
        "# Announcements are grouped into channels. A channel has its own interval and",
        "# its own rotation, so tips can run every five minutes while shop adverts run",
        "# every twenty. Messages join a channel by name in scheduledMessages.yml; one",
        "# that names none joins 'default'.",
        "#",
        "# Time format:",
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

    @Comment({
            "#",
            "# Master switch. Turning this off pauses every channel at once, which is",
            "# what /automessage disable does.",
            "#"
    })
    public boolean enabled = true;

    @Comment({
            "#",
            "# Language served to players whose own language has no file in lang/.",
            "#"
    })
    public String fallbackLanguage = "en";

    @Comment({
            "#",
            "# Languages written out on first run. Anything else you drop into lang/ is",
            "# picked up automatically - this list only decides what a fresh install",
            "# starts with, and removing a code from it does not delete its file.",
            "#"
    })
    public List<String> languages = List.of("en", "pl", "de");

    @Comment({
            "#",
            "# The announcement channels. Every one is written out in full - there is no",
            "# implicit channel and no setting outside this list.",
            "#",
            "#   name          What messages refer to in scheduledMessages.yml. Matched",
            "#                 ignoring case. 'default' is where a message with no",
            "#                 channel of its own lands.",
            "#   enabled       Turns this one channel off without deleting it.",
            "#   initialDelay  How long after startup its first message is sent.",
            "#   period        How much time passes between two of its messages.",
            "#   selector      Which message it picks next:",
            "#                   SHUFFLE     random order, every message shown once",
            "#                               before any repeats. Usually what you want.",
            "#                   SEQUENTIAL  straight down the list, in order.",
            "#                   RANDOM      drawn independently - can repeat back to back.",
            "#                   WEIGHTED    random, biased by each message's 'weight'.",
            "#",
            "# To add a stream, copy an entry and give it a new name:",
            "#",
            "#   - name: ads",
            "#     enabled: true",
            "#     initialDelay: 2m",
            "#     period: 20m",
            "#     selector: SHUFFLE",
            "#",
            "# then put 'channel: ads' on the messages that belong to it.",
            "#"
    })
    public List<AnnouncementChannel> channels = List.of(
            new AnnouncementChannel(
                    AnnouncementChannel.DEFAULT_NAME,
                    true,
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(5),
                    MessageSelectorType.SHUFFLE
            )
    );

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return every configured channel; a file with none leaves the plugin silent, which is
     *         reported rather than guessed at
     */
    public List<AnnouncementChannel> channels() {
        return List.copyOf(channels);
    }

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> registry.register(new AnnouncementChannelSerializer());
    }

    @Override
    public String getFileName() {
        return "config.yml";
    }
}
