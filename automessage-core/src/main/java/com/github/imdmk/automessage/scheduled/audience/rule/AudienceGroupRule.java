package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.shared.validate.Validator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record AudienceGroupRule(@NotNull String group) implements AudienceRule {

    private static final String GROUP_PREFIX = "group.";

    public AudienceGroupRule {
        Validator.notNull(group, "group");
    }

    @Override
    public boolean test(@NotNull Player player) {
        String permission = GROUP_PREFIX + group;
        return player.hasPermission(permission);
    }

    @Override
    public @NotNull Type type() {
        return Type.GROUP;
    }
}

