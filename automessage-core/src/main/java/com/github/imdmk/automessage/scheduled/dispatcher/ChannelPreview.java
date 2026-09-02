package com.github.imdmk.automessage.scheduled.dispatcher;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * What a channel is about to announce and when, or why that cannot be answered.
 */
public record ChannelPreview(
        String channel,
        Kind kind,
        @Nullable String message,
        @Nullable Duration due
) {

    public enum Kind {
        NEXT,
        UNPREDICTABLE,
        DISABLED,
        EMPTY
    }

    static ChannelPreview next(String channel, String message, @Nullable Duration due) {
        return new ChannelPreview(channel, Kind.NEXT, message, due);
    }

    // A RANDOM channel draws when it fires, so any message named now would be a different one
    // from the one that arrives - but the time it arrives at is still known.
    static ChannelPreview unpredictable(String channel, @Nullable Duration due) {
        return new ChannelPreview(channel, Kind.UNPREDICTABLE, null, due);
    }

    static ChannelPreview disabled(String channel) {
        return new ChannelPreview(channel, Kind.DISABLED, null, null);
    }

    static ChannelPreview empty(String channel) {
        return new ChannelPreview(channel, Kind.EMPTY, null, null);
    }
}
