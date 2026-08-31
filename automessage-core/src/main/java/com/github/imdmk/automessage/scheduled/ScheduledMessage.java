package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public record ScheduledMessage(
        String name,
        @Unmodifiable List<AudienceRule> rules,
        int weight,
        String channel,
        @Nullable MessageTrigger trigger) {

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

        if (weight < 0) {
            throw new IllegalArgumentException("weight must not be negative, got " + weight);
        }

        // Normalised once here so the dispatcher can group messages by a plain string equality
        // check instead of case-folding every message on every tick.
        channel = AnnouncementChannel.normalize(channel);

        rules = List.copyOf(rules);
    }

    /**
     * A message that configures nothing weighs the default, joins the default channel and takes
     * part in the timed rotation.
     */
    public ScheduledMessage(String name, List<AudienceRule> rules) {
        this(name, rules, DEFAULT_WEIGHT, AnnouncementChannel.DEFAULT_NAME, null);
    }

    public boolean belongsTo(AnnouncementChannel announcementChannel) {
        return announcementChannel.matches(channel);
    }

    /**
     * A triggered message fires on its event and never through the rotation; leaving it in both
     * would announce it at random moments as well as the one it was written for.
     */
    public boolean isScheduled() {
        return trigger == null;
    }

}
