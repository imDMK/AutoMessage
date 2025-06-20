package com.github.imdmk.automessage.feature.message.auto.eligibility;

import com.github.imdmk.automessage.feature.message.auto.AutoMessageNotice;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DefaultEligibilityEvaluator implements AutoMessageEligibilityEvaluator {

    private static final String LUCK_PERMS_GROUP_PREFIX = "group.";

    @Override
    public boolean canReceive(@NotNull Player player, @NotNull AutoMessageNotice notice) {
        return notice.getRequiredPermission()
                .map(player::hasPermission)
                .orElse(true)
                && notice.getRequiredGroup()
                .map(rank -> player.hasPermission(LUCK_PERMS_GROUP_PREFIX + rank))
                .orElse(true);
    }
}
