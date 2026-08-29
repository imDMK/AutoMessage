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
        String channel) {

    public ScheduledMessage {
        Objects.requireNonNull(name, "name");

        // Normalised once here so the dispatcher can group messages by a plain string equality
        // check instead of case-folding every message on every tick.
        channel = AnnouncementChannel.normalize(channel);

        if (notices.isEmpty()) {
            throw new IllegalArgumentException("notices must contains at least one notice");
        }

        notices = List.copyOf(notices);
        rules = List.copyOf(rules);
    }

    /** A message that does not name a channel belongs to the default one. */
    public ScheduledMessage(
            String name,
            List<Notice> notices,
            List<AudienceRule> rules
    ) {
        this(name, notices, rules, AnnouncementChannel.DEFAULT_NAME);
    }

    public boolean belongsTo(AnnouncementChannel announcementChannel) {
        return announcementChannel.matches(channel);
    }
}
