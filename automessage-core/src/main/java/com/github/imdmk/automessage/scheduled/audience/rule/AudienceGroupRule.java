package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;

public record AudienceGroupRule(String group) implements AudienceRule {

    private static final String GROUP_PREFIX = "group.";

    @Override
    public boolean test(Player player) {
        final String permission = GROUP_PREFIX + group;
        return player.hasPermission(permission);
    }

    @Override
    public Type type() {
        return Type.GROUP;
    }
}

