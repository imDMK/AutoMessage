package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.shared.validate.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ScheduledMessageBuilder {

    private String name;
    private final List<Notice> notices = new ArrayList<>();
    private final List<AudienceRule> rules = new ArrayList<>();

    public static ScheduledMessageBuilder create() {
        return new ScheduledMessageBuilder();
    }

    public @NotNull ScheduledMessageBuilder name(@NotNull String name) {
        this.name = Validator.notNull(name, "name");
        return this;
    }

    public @NotNull ScheduledMessageBuilder addNotice(@NotNull Notice notice) {
        this.notices.add(Validator.notNull(notice, "notice"));
        return this;
    }

    public @NotNull ScheduledMessageBuilder addNotices(@NotNull List<Notice> notices) {
        Validator.notNull(notices, "notices");
        notices.forEach(n -> this.notices.add(Validator.notNull(n, "notice element")));
        return this;
    }

    public @NotNull ScheduledMessageBuilder addNotices(@NotNull Notice... notices) {
        return addNotices(List.of(notices));
    }

    public @NotNull ScheduledMessageBuilder addRule(@NotNull AudienceRule rule) {
        this.rules.add(Validator.notNull(rule, "rule"));
        return this;
    }

    public @NotNull ScheduledMessageBuilder addRules(@NotNull List<AudienceRule> rules) {
        Validator.notNull(rules, "rules");
        rules.forEach(r -> this.rules.add(Validator.notNull(r, "rule element")));
        return this;
    }

    public @NotNull ScheduledMessageBuilder addRules(@NotNull AudienceRule... rules) {
        return addRules(List.of(rules));
    }

    public @NotNull ScheduledMessage build() {
        Validator.notNull(this.name, "name is required");

        return new ScheduledMessage(
                this.name,
                List.copyOf(this.notices),
                List.copyOf(this.rules)
        );
    }
}
