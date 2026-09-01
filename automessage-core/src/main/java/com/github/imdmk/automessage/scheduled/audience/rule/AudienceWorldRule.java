package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    public boolean test(Viewer viewer, AudienceContext context) {
        // A viewer with no world - the console, or anyone on a proxy - is in none of them, so
        // the rule simply does not match rather than throwing.
        return viewer.world()
                .map(world -> worlds.contains(world.toLowerCase(Locale.ROOT)))
                .orElse(false);
    }

    @Override
    public Type type() {
        return Type.WORLD;
    }
}
