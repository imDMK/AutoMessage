package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.Viewer;

public record AudiencePermissionRule(String permission) implements AudienceRule {

    @Override
    public boolean test(Viewer viewer, AudienceContext context) {
        return viewer.hasPermission(permission);
    }

    @Override
    public Type type() {
        return Type.PERMISSION;
    }
}
