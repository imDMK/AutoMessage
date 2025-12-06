package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public sealed interface AudienceRule
        permits AudiencePermissionRule, AudienceGroupRule {

    static AudienceGroupRule group(String group) {
        return new AudienceGroupRule(group);
    }

    static AudiencePermissionRule permission(String permission) {
        return new AudiencePermissionRule(permission);
    }

    boolean test(@NotNull Player player);

    enum Type {
        PERMISSION,
        GROUP
    }

    @NotNull
    Type type();
}

