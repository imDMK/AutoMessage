package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Passes when at least one nested rule passes.
 *
 * <p>
 * The rules listed on a message are combined with AND, which cannot express "VIP or moderator".
 * Splitting that into two near-identical messages is the workaround this removes.
 * </p>
 */
public record AudienceAnyOfRule(@Unmodifiable List<AudienceRule> rules) implements AudienceRule {

    public AudienceAnyOfRule {
        if (rules.isEmpty()) {
            throw new IllegalArgumentException("ANY_OF must contain at least one nested rule");
        }

        rules = List.copyOf(rules);
    }

    public static AudienceAnyOfRule of(AudienceRule... rules) {
        return new AudienceAnyOfRule(List.of(rules));
    }

    @Override
    public boolean test(Player player) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).test(player)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Type type() {
        return Type.ANY_OF;
    }
}
