package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Passes only when none of the nested rules pass.
 *
 * <p>
 * Typically used to exclude a group from an otherwise public announcement — hiding the "buy a
 * rank" advert from the players who already bought one.
 * </p>
 */
public record AudienceNoneOfRule(@Unmodifiable List<AudienceRule> rules) implements AudienceRule {

    public AudienceNoneOfRule {
        if (rules.isEmpty()) {
            throw new IllegalArgumentException("NONE_OF must contain at least one nested rule");
        }

        rules = List.copyOf(rules);
    }

    public static AudienceNoneOfRule of(AudienceRule... rules) {
        return new AudienceNoneOfRule(List.of(rules));
    }

    @Override
    public boolean test(Player player) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).test(player)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Type type() {
        return Type.NONE_OF;
    }
}
