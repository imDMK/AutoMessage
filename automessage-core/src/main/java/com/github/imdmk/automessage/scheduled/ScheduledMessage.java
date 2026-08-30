package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public record ScheduledMessage(
        String name,
        @Unmodifiable List<Notice> notices,
        @Unmodifiable List<AudienceRule> rules,
        int weight,
        String channel) {

    /**
     * Relative weight of a message that does not configure one.
     *
     * <p>
     * Every message sharing the default is what makes {@code WEIGHTED} behave exactly like
     * {@code RANDOM} until somebody actually tunes a weight.
     * </p>
     */
    public static final int DEFAULT_WEIGHT = 1;

    public ScheduledMessage {
        Objects.requireNonNull(name, "name");

        if (notices.isEmpty()) {
            throw new IllegalArgumentException("notices must contains at least one notice");
        }

        if (weight < 0) {
            throw new IllegalArgumentException("weight must not be negative, got " + weight);
        }

        // Normalised once here so the dispatcher can group messages by a plain string equality
        // check instead of case-folding every message on every tick.
        channel = AnnouncementChannel.normalize(channel);

        notices = List.copyOf(notices);
        rules = List.copyOf(rules);
    }

    /** A message that configures neither weighs the default and joins the default channel. */
    public ScheduledMessage(
            String name,
            List<Notice> notices,
            List<AudienceRule> rules
    ) {
        this(name, notices, rules, DEFAULT_WEIGHT, AnnouncementChannel.DEFAULT_NAME);
    }

    public boolean belongsTo(AnnouncementChannel announcementChannel) {
        return announcementChannel.matches(channel);
    }
}
