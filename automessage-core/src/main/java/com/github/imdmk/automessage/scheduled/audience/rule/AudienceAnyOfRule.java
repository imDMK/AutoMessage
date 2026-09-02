package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

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
    public boolean test(Viewer viewer, AudienceContext context) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).test(viewer, context)) {
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
