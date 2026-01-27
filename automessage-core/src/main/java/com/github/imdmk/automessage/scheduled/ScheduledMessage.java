package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public record ScheduledMessage(
        String name,
        @Unmodifiable List<Notice> notices,
        @Unmodifiable List<AudienceRule> rules) {

    public ScheduledMessage {
        Objects.requireNonNull(name, "name");

        if (notices.isEmpty()) {
            throw new IllegalArgumentException("notices must contains at least one notice");
        }

        notices = List.copyOf(notices);
        rules = List.copyOf(rules);
    }
}
