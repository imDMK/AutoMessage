package com.github.imdmk.automessage.scheduled.dispatcher;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

/**
 * Who an announcement is being sent to.
 *
 * <p>
 * Audience rules are applied by the dispatcher rather than by the target, so a target only has to
 * answer who is in scope: one player for a triggered message, everyone online for a scheduled one.
 * </p>
 */
public interface DispatchTarget {

    Collection<? extends Player> recipients();

    static DispatchTarget player(Player player) {
        return new DispatchPlayersTarget(List.of(player));
    }

    static DispatchTarget players(Collection<? extends Player> players) {
        return new DispatchPlayersTarget(players);
    }
}
