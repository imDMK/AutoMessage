package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.shared.validate.Validator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public record DispatchPlayersTarget(
        @NotNull Collection<? extends Player> targets
) implements DispatchTarget {

    public DispatchPlayersTarget {
        Validator.notNull(targets, "targets cannot be null");
    }

    @Override
    public @NotNull Collection<? extends Player> recipients() {
        return targets;
    }
}
