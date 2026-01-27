package com.github.imdmk.automessage.scheduled.audience.filter;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.bukkit.entity.Player;

public interface AudienceFilter {

    boolean allows(Player player, ScheduledMessage message);

    static AudienceFilter ruleFilter() {
        return new RuleAudienceFilter();
    }
}
