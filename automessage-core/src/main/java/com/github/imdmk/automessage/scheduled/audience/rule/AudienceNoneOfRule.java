package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

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
    public boolean test(Viewer viewer, AudienceContext context) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).test(viewer, context)) {
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
