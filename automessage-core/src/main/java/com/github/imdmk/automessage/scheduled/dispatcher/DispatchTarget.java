package com.github.imdmk.automessage.scheduled.dispatcher;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface DispatchTarget {

    @NotNull Collection<? extends Player> recipients();

    static @NotNull DispatchTarget player(@NotNull Player player) {
        return new DispatchPlayersTarget(List.of(player));
    }

    static @NotNull DispatchTarget players(@NotNull Collection<? extends Player> players) {
        return new DispatchPlayersTarget(players);
    }

    static @NotNull DispatchTarget filtered(
            @NotNull Collection<? extends Player> players,
            @NotNull Predicate<Player> filter
    ) {
        return new DispatchFilteredTarget(players, filter);
    }
}
