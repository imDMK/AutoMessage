package com.github.imdmk.automessage.scheduled.audience.filter;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Determines whether a given player is allowed to receive a specific {@link ScheduledMessage}.
 *
 * <p>Audience filters enable selective delivery based on rules such as permissions,
 * groups, worlds, conditions, or any custom logic implemented in {@link AudienceFilterImpl}.</p>
 */
public interface AudienceFilter {

    /**
     * Evaluates whether the given player should receive the specified scheduled message.
     *
     * @param player  the player being considered (never null)
     * @param message the scheduled message evaluated against the player (never null)
     * @return true if the player is allowed to receive the message; false otherwise
     */
    boolean allows(@NotNull Player player, @NotNull ScheduledMessage message);

    /**
     * Creates the default audience filter used by the plugin.
     *
     * <p>The default implementation respects all {@link com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule}
     * entries declared on the scheduled message. It does not introduce any implicit filtering.</p>
     *
     * @return a new default {@link AudienceFilter} instance
     */
    static AudienceFilter createDefault() {
        return new AudienceFilterImpl();
    }
}
