package com.github.imdmk.automessage.scheduled.audience.filter;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceContext;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.platform.viewer.Viewer;

import java.util.List;

final class RuleAudienceFilter implements AudienceFilter {

    @Override
    public boolean allows(Viewer viewer, ScheduledMessage message, AudienceContext context) {
        // Called once per online player per broadcast; an indexed loop keeps that free of the
        // stream and lambda capture a stream pipeline would allocate on every call.
        final List<AudienceRule> rules = message.rules();

        for (int i = 0; i < rules.size(); i++) {
            if (!rules.get(i).test(viewer, context)) {
                return false;
            }
        }

        return true;
    }
}

