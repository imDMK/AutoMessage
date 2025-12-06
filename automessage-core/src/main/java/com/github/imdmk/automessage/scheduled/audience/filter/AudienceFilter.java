package com.github.imdmk.automessage.scheduled.audience.filter;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface AudienceFilter {

    boolean allows(@NotNull Player player, @NotNull ScheduledMessage message);

    static AudienceFilter createDefault() {
        return new AudienceFilterImpl();
    }
}
