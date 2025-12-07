package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.shared.validate.Validator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Simple dispatch target representing a predefined collection of recipient players.
 *
 * <p>This implementation performs no filtering or transformation.
 * The provided collection is used as-is when dispatching messages.</p>
 */
public record DispatchPlayersTarget(
        @NotNull Collection<? extends Player> players
) implements DispatchTarget {

    public DispatchPlayersTarget {
        Validator.notNull(players, "players cannot be null");
    }

    @Override
    public @NotNull Collection<? extends Player> recipients() {
        return players;
    }
}
