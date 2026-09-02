package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.Viewer;

public record AudienceGroupRule(String group) implements AudienceRule {

    private static final String GROUP_PREFIX = "group.";

    @Override
    public boolean test(Viewer viewer, AudienceContext context) {
        final String permission = GROUP_PREFIX + group;
        return viewer.hasPermission(permission);
    }

    @Override
    public Type type() {
        return Type.GROUP;
    }
}

