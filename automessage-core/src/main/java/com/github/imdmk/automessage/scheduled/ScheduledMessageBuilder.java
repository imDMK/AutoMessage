package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.locale.MessageTranslation;

import java.util.ArrayList;
import java.util.List;

public final class ScheduledMessageBuilder {

    private String name;

    private final List<Notice> notices = new ArrayList<>();
    private final List<AudienceRule> rules = new ArrayList<>();
    private final List<MessageTranslation> translations = new ArrayList<>();

    public static ScheduledMessageBuilder create() {
        return new ScheduledMessageBuilder();
    }

    public ScheduledMessageBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ScheduledMessageBuilder addNotice(Notice notice) {
        this.notices.add(notice);
        return this;
    }

    public ScheduledMessageBuilder addNotices(List<Notice> notices) {
        this.notices.addAll(notices);
        return this;
    }

    public ScheduledMessageBuilder addNotices(Notice... notices) {
        return addNotices(List.of(notices));
    }

    public ScheduledMessageBuilder addRule(AudienceRule rule) {
        this.rules.add(rule);
        return this;
    }

    public ScheduledMessageBuilder addRules(List<AudienceRule> rules) {
        this.rules.addAll(rules);
        return this;
    }

    public ScheduledMessageBuilder addRules(AudienceRule... rules) {
        return addRules(List.of(rules));
    }

    public ScheduledMessageBuilder addTranslation(String locale, Notice... notices) {
        this.translations.add(new MessageTranslation(locale, List.of(notices)));
        return this;
    }

    public ScheduledMessageBuilder addTranslation(MessageTranslation translation) {
        this.translations.add(translation);
        return this;
    }

    public ScheduledMessage build() {
        return new ScheduledMessage(
                this.name,
                this.notices,
                this.rules,
                this.translations
        );
    }
}
