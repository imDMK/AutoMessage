package com.github.imdmk.automessage.scheduled.dispatcher;

import org.bukkit.entity.Player;

import java.util.Collection;

public record DispatchPlayersTarget(
        Collection<? extends Player> players
) implements DispatchTarget {

    @Override
    public Collection<? extends Player> recipients() {
        return players;
    }
}
