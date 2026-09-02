package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.Viewer;

public record AudienceNotRule(AudienceRule rule) implements AudienceRule {

    @Override
    public boolean test(Viewer viewer, AudienceContext context) {
        return !rule.test(viewer, context);
    }

    @Override
    public Type type() {
        return Type.NOT;
    }
}
