package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public record AudiencePlayTimeRule(Duration minimum, @Nullable Duration maximum) implements AudienceRule {

    public AudiencePlayTimeRule {
        if (minimum == null || minimum.isNegative()) {
            throw new IllegalArgumentException("minimum must be a non-negative duration");
        }

        if (maximum != null && maximum.compareTo(minimum) < 0) {
            throw new IllegalArgumentException("maximum must not be below minimum");
        }
    }

    @Override
    public boolean test(Viewer viewer, AudienceContext context) {
        // A platform that keeps no statistics answers with nothing, and the rule does not match.
        // Treating "unknown" as zero would make every veteran look like a newcomer.
        return context.playtime().playtimeOf(viewer)
                .map(this::isInRange)
                .orElse(false);
    }

    private boolean isInRange(Duration played) {
        if (played.compareTo(minimum) < 0) {
            return false;
        }

        return maximum == null || played.compareTo(maximum) <= 0;
    }

    @Override
    public Type type() {
        return Type.PLAYTIME;
    }
}
