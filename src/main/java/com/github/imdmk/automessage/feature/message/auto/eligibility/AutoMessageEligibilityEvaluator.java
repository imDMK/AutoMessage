package com.github.imdmk.automessage.feature.message.auto.eligibility;

import com.github.imdmk.automessage.feature.message.auto.AutoMessageNotice;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Evaluates whether a player is eligible to receive a specific auto message notice.
 */
public interface AutoMessageEligibilityEvaluator {

    /**
     * Determines whether the given player is eligible to receive the specified auto message notice.
     *
     * @param player the player to evaluate, must not be {@code null}
     * @param notice the auto message notice to check eligibility against, must not be {@code null}
     * @return {@code true} if the player is eligible to receive the notice; {@code false} otherwise
     */
    boolean canReceive(@NotNull Player player, @NotNull AutoMessageNotice notice);

}
