package com.github.imdmk.automessage.scheduled.dispatcher;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a group of players to whom a scheduled message should be dispatched.
 *
 * <p>This abstraction allows the dispatcher to target:</p>
 * <ul>
 *   <li>a single player,</li>
 *   <li>a fixed collection of players,</li>
 *   <li>or a dynamically filtered subset of players.</li>
 * </ul>
 *
 * <p>Implementations are expected to be immutable and lightweight.</p>
 */
public interface DispatchTarget {

    /**
     * Returns the collection of players who should receive the message.
     * <p>This method never returns null, but may return an empty collection.</p>
     *
     * @return a non-null collection of target recipients
     */
    @NotNull Collection<? extends Player> recipients();

    /**
     * Creates a dispatch target consisting of a single player.
     *
     * @param player a non-null player
     * @return a dispatch target containing exactly this player
     */
    static @NotNull DispatchTarget player(@NotNull Player player) {
        return new DispatchPlayersTarget(List.of(player));
    }

    /**
     * Creates a dispatch target from a provided collection of players.
     *
     * @param players a non-null collection of players
     * @return a dispatch target for these players
     */
    static @NotNull DispatchTarget players(@NotNull Collection<? extends Player> players) {
        return new DispatchPlayersTarget(players);
    }

    /**
     * Creates a dispatch target based on a collection of players filtered by a predicate.
     *
     * <p>The filter is evaluated lazily by the target implementation whenever
     * {@link #recipients()} is invoked. This allows the dispatcher to handle
     * dynamic state changes (permissions, groups, world, AFK status, etc.).</p>
     *
     * @param players a non-null base collection of players
     * @param filter  a non-null predicate used to determine eligibility
     * @return a filtered dispatch target
     */
    static @NotNull DispatchTarget filtered(
            @NotNull Collection<? extends Player> players,
            @NotNull Predicate<Player> filter
    ) {
        return new DispatchFilteredTarget(players, filter);
    }
}
