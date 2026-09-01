package com.github.imdmk.automessage.scheduled.audience.filter;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceContext;

public interface AudienceFilter {

    boolean allows(Viewer viewer, ScheduledMessage message, AudienceContext context);

    static AudienceFilter ruleFilter() {
        return new RuleAudienceFilter();
    }
}
