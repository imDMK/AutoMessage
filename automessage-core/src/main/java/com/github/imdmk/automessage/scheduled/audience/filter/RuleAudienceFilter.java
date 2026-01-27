package com.github.imdmk.automessage.scheduled.audience.filter;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.bukkit.entity.Player;

final class RuleAudienceFilter implements AudienceFilter {

    @Override
    public boolean allows(Player player, ScheduledMessage message) {
        return message.rules()
                .stream()
                .allMatch(rule -> rule.test(player));
    }
}

