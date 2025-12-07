package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.shared.validate.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public record ScheduledMessage(
        @NotNull String name,
        @NotNull @Unmodifiable List<Notice> notices,
        @NotNull @Unmodifiable List<AudienceRule> rules) {

    public ScheduledMessage {
        Validator.notNull(name, "name cannot be null");
        Validator.notNull(notices, "notices cannot be null");
        Validator.notNull(rules, "rules cannot be null");

        if (notices.isEmpty()) {
            throw new IllegalArgumentException("ScheduledMessage must contain at least one Notice");
        }

        notices = List.copyOf(notices);
        rules = List.copyOf(rules);
    }
}
