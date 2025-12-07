package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.shared.validate.Validator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record AudiencePermissionRule(@NotNull String permission) implements AudienceRule {

    public AudiencePermissionRule {
        Validator.notNull(permission, "permission");
    }

    @Override
    public boolean test(@NotNull Player player) {
        return player.hasPermission(permission);
    }

    @Override
    public @NotNull Type type() {
        return Type.PERMISSION;
    }
}
