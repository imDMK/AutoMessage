package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.shared.validate.Validator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Predicate;

public record DispatchFilteredTarget(
        @NotNull Collection<? extends Player> targets,
        @NotNull Predicate<Player> filter
) implements DispatchTarget {

    public DispatchFilteredTarget {
        Validator.notNull(targets, "targets cannot be null");
        Validator.notNull(filter, "filter cannot be null");
    }

    @Override
    public @NotNull Collection<? extends Player> recipients() {
        return targets.stream()
                .filter(filter)
                .toList();
    }
}
