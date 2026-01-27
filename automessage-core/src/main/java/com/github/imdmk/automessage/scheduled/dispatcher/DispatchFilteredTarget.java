package com.github.imdmk.automessage.scheduled.dispatcher;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.Predicate;

public record DispatchFilteredTarget(
        Collection<? extends Player> players,
        Predicate<Player> predicate
) implements DispatchTarget {

    @Override
    public Collection<? extends Player> recipients() {
        return players.stream()
                .filter(predicate)
                .toList(); // safe: creates an immutable snapshot view
    }
}
