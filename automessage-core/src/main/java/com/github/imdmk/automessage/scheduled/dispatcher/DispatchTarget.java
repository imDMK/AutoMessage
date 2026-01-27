package com.github.imdmk.automessage.scheduled.dispatcher;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface DispatchTarget {

    Collection<? extends Player> recipients();

    static DispatchTarget player(Player player) {
        return new DispatchPlayersTarget(List.of(player));
    }

    static DispatchTarget players(Collection<? extends Player> players) {
        return new DispatchPlayersTarget(players);
    }

    static DispatchTarget filtered(
            Collection<? extends Player> players,
            Predicate<Player> filter
    ) {
        return new DispatchFilteredTarget(players, filter);
    }
}
