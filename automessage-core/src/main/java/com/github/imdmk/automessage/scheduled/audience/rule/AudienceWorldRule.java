package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Restricts a message to players standing in one of the named worlds.
 *
 * <p>
 * This is what keeps lobby advertising out of the event arena, and minigame instructions out of
 * the survival world.
 * </p>
 */
public record AudienceWorldRule(@Unmodifiable Set<String> worlds) implements AudienceRule {

    public AudienceWorldRule {
        // World names are matched case-insensitively and normalised once here, rather than
        // lower-casing the player's world on every broadcast.
        final Set<String> normalized = new LinkedHashSet<>();

        for (final String world : worlds) {
            normalized.add(world.toLowerCase(Locale.ROOT));
        }

        worlds = Set.copyOf(normalized);
    }

    public static AudienceWorldRule of(String... worlds) {
        return new AudienceWorldRule(Set.copyOf(List.of(worlds)));
    }

    @Override
    public boolean test(Player player) {
        return worlds.contains(player.getWorld().getName().toLowerCase(Locale.ROOT));
    }

    @Override
    public Type type() {
        return Type.WORLD;
    }
}
