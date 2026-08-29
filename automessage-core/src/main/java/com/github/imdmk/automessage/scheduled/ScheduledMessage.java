package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public record ScheduledMessage(
        String name,
        @Unmodifiable List<Notice> notices,
        @Unmodifiable List<AudienceRule> rules,
        @Nullable MessageTrigger trigger) {

    public ScheduledMessage {
        Objects.requireNonNull(name, "name");

        if (notices.isEmpty()) {
            throw new IllegalArgumentException("notices must contains at least one notice");
        }

        notices = List.copyOf(notices);
        rules = List.copyOf(rules);
    }

    /** A message without a trigger is part of the timed rotation. */
    public ScheduledMessage(
            String name,
            List<Notice> notices,
            List<AudienceRule> rules
    ) {
        this(name, notices, rules, null);
    }

    /**
     * A triggered message fires on its event and never through the rotation; leaving it in both
     * would announce it at random moments as well as the one it was written for.
     */
    public boolean isScheduled() {
        return trigger == null;
    }
}
