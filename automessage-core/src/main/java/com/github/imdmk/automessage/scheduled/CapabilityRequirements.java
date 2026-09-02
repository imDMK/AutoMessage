package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.capability.Capability;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceAnyOfRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceGroupRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceNoneOfRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceNotRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudiencePermissionRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudiencePlayTimeRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudiencePlayerCountRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceWorldRule;
import com.github.imdmk.automessage.scheduled.trigger.JoinTrigger;

import java.util.EnumSet;
import java.util.Set;

public final class CapabilityRequirements {

    private CapabilityRequirements() {
    }

    public static boolean satisfiedBy(ScheduledMessage message, Capabilities capabilities) {
        return requiredBy(message).stream().allMatch(capabilities::supports);
    }

    public static Set<Capability> requiredBy(ScheduledMessage message) {
        final Set<Capability> required = EnumSet.noneOf(Capability.class);

        message.rules().forEach(rule -> collect(rule, required));

        // Only the first-join variant needs anything special: a plain join is something every
        // platform can see happen.
        if (message.trigger() instanceof JoinTrigger join && join.firstJoinOnly()) {
            required.add(Capability.FIRST_JOIN_TRIGGER);
        }

        return required;
    }

    private static void collect(AudienceRule rule, Set<Capability> required) {
        switch (rule) {
            case AudienceWorldRule ignored -> required.add(Capability.WORLD_RULE);
            case AudiencePlayTimeRule ignored -> required.add(Capability.PLAYTIME_RULE);
            case AudienceGroupRule ignored -> required.add(Capability.GROUP_RULE);
            case AudiencePermissionRule ignored -> required.add(Capability.PERMISSION_RULE);

            // The online count is something every platform with players can answer.
            case AudiencePlayerCountRule ignored -> {
            }

            case AudienceAnyOfRule anyOf -> anyOf.rules().forEach(nested -> collect(nested, required));
            case AudienceNoneOfRule noneOf -> noneOf.rules().forEach(nested -> collect(nested, required));
            case AudienceNotRule not -> collect(not.rule(), required);
        }
    }
}
