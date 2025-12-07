package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.shared.validate.Validator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Dispatch target representing a collection of players filtered by a predicate.
 *
 * <p>Filtering is performed on each call to {@link #recipients()}, ensuring the
 * result always reflects the current online player state and rule logic.</p>
 */
public record DispatchFilteredTarget(
        @NotNull Collection<? extends Player> players,
        @NotNull Predicate<Player> predicate
) implements DispatchTarget {

    public DispatchFilteredTarget {
        Validator.notNull(players, "players cannot be null");
        Validator.notNull(predicate, "predicate cannot be null");
    }

    @Override
    public @NotNull Collection<? extends Player> recipients() {
        return players.stream()
                .filter(predicate)
                .toList(); // safe: creates an immutable snapshot view
    }
}
