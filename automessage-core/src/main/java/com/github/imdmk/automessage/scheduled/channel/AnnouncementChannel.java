package com.github.imdmk.automessage.scheduled.channel;

import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

public record AnnouncementChannel(
        String name,
        boolean enabled,
        Duration initialDelay,
        Duration period,
        MessageSelectorType selector
) {

    public static final String DEFAULT_NAME = "default";

    public AnnouncementChannel {
        Objects.requireNonNull(name, "name");

        if (name.isBlank()) {
            throw new IllegalArgumentException("channel name must not be blank");
        }

        // initialDelay and period are deliberately allowed to be null here. They come straight
        // out of a hand-edited YAML file, and normalising them - along with negative and
        // sub-tick values - is DispatchTiming's job, which reports each correction to the
        // console. Rejecting them here would turn a fixable typo into a failed startup.
    }

    public boolean matches(String channelName) {
        return normalize(name).equals(normalize(channelName));
    }

    public static String normalize(String channelName) {
        return channelName == null
                ? DEFAULT_NAME
                : channelName.trim().toLowerCase(Locale.ROOT);
    }
}
