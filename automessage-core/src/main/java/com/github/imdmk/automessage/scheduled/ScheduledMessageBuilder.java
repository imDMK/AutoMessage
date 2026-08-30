package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;

import java.util.ArrayList;
import java.util.List;

public final class ScheduledMessageBuilder {

    private String name;
    private int weight = ScheduledMessage.DEFAULT_WEIGHT;
    private String channel = AnnouncementChannel.DEFAULT_NAME;
    private MessageTrigger trigger;

    private final List<Notice> notices = new ArrayList<>();
    private final List<AudienceRule> rules = new ArrayList<>();

    public static ScheduledMessageBuilder create() {
        return new ScheduledMessageBuilder();
    }

    public ScheduledMessageBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ScheduledMessageBuilder weight(int weight) {
        this.weight = weight;
        return this;
    }

    public ScheduledMessageBuilder channel(String channel) {
        this.channel = channel;
        return this;
    }

    public ScheduledMessageBuilder trigger(MessageTrigger trigger) {
        this.trigger = trigger;
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

    public ScheduledMessage build() {
        return new ScheduledMessage(
                this.name,
                this.notices,
                this.rules,
                this.weight,
                this.channel,
                this.trigger
        );
    }
}
