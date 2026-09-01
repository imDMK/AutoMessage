package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;

public record AudienceContext(ViewerRegistry viewers, PlaytimeSource playtime) {

    public static AudienceContext of(ViewerRegistry viewers, PlaytimeSource playtime) {
        return new AudienceContext(viewers, playtime);
    }
}
