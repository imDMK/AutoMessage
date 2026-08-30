package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;

/**
 * Inverts a single nested rule.
 */
public record AudienceNotRule(AudienceRule rule) implements AudienceRule {

    @Override
    public boolean test(Player player) {
        return !rule.test(player);
    }

    @Override
    public Type type() {
        return Type.NOT;
    }
}
