package com.github.imdmk.automessage.scheduled.dispatcher;

import org.jetbrains.annotations.Nullable;

/**
 * What a channel is about to announce, or why that cannot be answered.
 */
public record ChannelPreview(String channel, Kind kind, @Nullable String message) {

    public enum Kind {
        NEXT,
        UNPREDICTABLE,
        DISABLED,
        EMPTY
    }

    static ChannelPreview next(String channel, String message) {
        return new ChannelPreview(channel, Kind.NEXT, message);
    }

    // A RANDOM channel draws when it fires, so any answer given now would be a different message
    // from the one that actually arrives.
    static ChannelPreview unpredictable(String channel) {
        return new ChannelPreview(channel, Kind.UNPREDICTABLE, null);
    }

    static ChannelPreview disabled(String channel) {
        return new ChannelPreview(channel, Kind.DISABLED, null);
    }

    static ChannelPreview empty(String channel) {
        return new ChannelPreview(channel, Kind.EMPTY, null);
    }
}
