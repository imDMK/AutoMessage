package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.Viewer;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public sealed interface AudienceRule
        permits AudiencePermissionRule,
                AudienceGroupRule,
                AudienceWorldRule,
                AudiencePlayerCountRule,
                AudiencePlayTimeRule,
                AudienceAnyOfRule,
                AudienceNoneOfRule,
                AudienceNotRule {

    static AudienceGroupRule group(String group) {
        return new AudienceGroupRule(group);
    }

    static AudiencePermissionRule permission(String permission) {
        return new AudiencePermissionRule(permission);
    }

    static AudienceWorldRule worlds(String... worlds) {
        return AudienceWorldRule.of(worlds);
    }

    static AudienceWorldRule worlds(Set<String> worlds) {
        return new AudienceWorldRule(worlds);
    }

    static AudiencePlayerCountRule playerCount(int minimum, int maximum) {
        return new AudiencePlayerCountRule(minimum, maximum);
    }

    static AudiencePlayTimeRule playTime(Duration minimum, Duration maximum) {
        return new AudiencePlayTimeRule(minimum, maximum);
    }

    static AudienceAnyOfRule anyOf(AudienceRule... rules) {
        return AudienceAnyOfRule.of(rules);
    }

    static AudienceAnyOfRule anyOf(List<AudienceRule> rules) {
        return new AudienceAnyOfRule(rules);
    }

    static AudienceNoneOfRule noneOf(AudienceRule... rules) {
        return AudienceNoneOfRule.of(rules);
    }

    static AudienceNoneOfRule noneOf(List<AudienceRule> rules) {
        return new AudienceNoneOfRule(rules);
    }

    static AudienceNotRule not(AudienceRule rule) {
        return new AudienceNotRule(rule);
    }

    boolean test(Viewer viewer, AudienceContext context);

    enum Type {
        PERMISSION,
        GROUP,
        WORLD,
        PLAYER_COUNT,
        PLAYTIME,
        ANY_OF,
        NONE_OF,
        NOT
    }

    Type type();
}
