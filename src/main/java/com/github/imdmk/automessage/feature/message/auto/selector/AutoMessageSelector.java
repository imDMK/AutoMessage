package com.github.imdmk.automessage.feature.message.auto.selector;

import com.github.imdmk.automessage.feature.message.auto.AutoMessageNotice;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Selects an appropriate auto message notice for a player from a list of available messages.
 */
public interface AutoMessageSelector {

    /**
     * Selects a suitable {@link AutoMessageNotice} for the given player based on the provided messages.
     *
     * @param player   the player for whom to select the message, must not be {@code null}
     * @param messages the list of available messages to select from, must not be {@code null}
     * @return an {@link Optional} containing the selected auto message notice if any is applicable; otherwise, empty
     */
    Optional<AutoMessageNotice> selectFor(@NotNull Player player, @NotNull List<AutoMessageNotice> messages);

    /**
     * Resets any internal state maintained by the selector, preparing it for fresh selection cycles.
     */
    void reset();
}
