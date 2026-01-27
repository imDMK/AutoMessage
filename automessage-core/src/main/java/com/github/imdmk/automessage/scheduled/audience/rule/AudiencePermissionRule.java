package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;

public record AudiencePermissionRule(String permission) implements AudienceRule {

    @Override
    public boolean test(Player player) {
        return player.hasPermission(permission);
    }

    @Override
    public Type type() {
        return Type.PERMISSION;
    }
}
