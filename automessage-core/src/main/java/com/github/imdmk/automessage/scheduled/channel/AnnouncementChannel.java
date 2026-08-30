package com.github.imdmk.automessage.scheduled.channel;

import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

/**
 * An independent announcement stream with its own schedule and rotation.
 *
 * <p>
 * A single global interval forces every announcement onto one rhythm: tips every five minutes and
 * a shop advert every twenty cannot both be right. Channels give each group of messages its own
 * timing, its own selector and its own on/off switch, and a message picks its channel by name.
 * </p>
 *
 * @param name         identifier used by messages to join this channel, matched ignoring case
 * @param enabled      whether this channel dispatches at all
 * @param initialDelay how long after startup this channel sends its first message; may be null or
 *                     invalid, and is normalised by {@code DispatchTiming}
 * @param period       how much time passes between two of this channel's messages; may be null or
 *                     invalid, and is normalised by {@code DispatchTiming}
 * @param selector     rotation strategy used within this channel
 */
public record AnnouncementChannel(
        String name,
        boolean enabled,
        Duration initialDelay,
        Duration period,
        MessageSelectorType selector
) {

    /** Channel a message joins when it does not name one. */
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

    /**
     * Channel names are matched ignoring case, so a message written as {@code channel: Ads} still
     * reaches a channel declared as {@code ads}.
     */
    public boolean matches(String channelName) {
        return normalize(name).equals(normalize(channelName));
    }

    public static String normalize(String channelName) {
        return channelName == null
                ? DEFAULT_NAME
                : channelName.trim().toLowerCase(Locale.ROOT);
    }

    public boolean isDefault() {
        return matches(DEFAULT_NAME);
    }
}
