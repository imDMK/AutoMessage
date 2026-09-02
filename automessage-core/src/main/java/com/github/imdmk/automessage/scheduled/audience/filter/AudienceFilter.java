package com.github.imdmk.automessage.scheduled.audience.filter;

import com.github.imdmk.automessage.scheduled.audience.optout.AnnouncementOptOut;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceContext;

public interface AudienceFilter {

    boolean allows(Viewer viewer, ScheduledMessage message, AudienceContext context);

    static AudienceFilter ruleFilter() {
        return new RuleAudienceFilter();
    }

    // A set lookup that rejects the whole player, so it belongs in front of any rule rather than
    // behind them: a muted veteran should not cost a playtime lookup to be turned away.
    static AudienceFilter notMuted(AnnouncementOptOut optOut) {
        return (viewer, message, context) -> !optOut.isMuted(viewer.uniqueId());
    }

    default AudienceFilter and(AudienceFilter other) {
        return (viewer, message, context) ->
                allows(viewer, message, context) && other.allows(viewer, message, context);
    }
}
