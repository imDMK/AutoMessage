package com.github.imdmk.automessage.scheduled.audience.filter;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.shared.validate.Validator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class AudienceFilterImpl implements AudienceFilter {

    @Override
    public boolean allows(@NotNull Player player, @NotNull ScheduledMessage message) {
        Validator.notNull(player, "player");
        Validator.notNull(message, "message");

        return message.rules().stream()
                    .allMatch(rule -> rule.test(player));
    }
}

