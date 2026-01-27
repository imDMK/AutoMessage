package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;

public sealed interface AudienceRule
        permits AudiencePermissionRule, AudienceGroupRule {

    static AudienceGroupRule group(String group) {
        return new AudienceGroupRule(group);
    }

    static AudiencePermissionRule permission(String permission) {
        return new AudiencePermissionRule(permission);
    }

    boolean test(Player player);

    enum Type {
        PERMISSION,
        GROUP
    }

    Type type();
}
