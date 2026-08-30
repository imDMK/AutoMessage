package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * One condition a player must satisfy to receive a message.
 *
 * <p>
 * The rules listed on a message are combined with AND. {@link AudienceAnyOfRule},
 * {@link AudienceNoneOfRule} and {@link AudienceNotRule} nest rules inside one another, which
 * turns that flat list into an expression tree and makes conditions like "VIP or moderator, but
 * not in the arena" expressible without duplicating the message.
 * </p>
 */
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

    boolean test(Player player);

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
