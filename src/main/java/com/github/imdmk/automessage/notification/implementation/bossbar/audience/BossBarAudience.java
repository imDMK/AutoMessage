package com.github.imdmk.automessage.notification.implementation.bossbar.audience;

import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;

import java.time.Duration;
import java.time.Instant;

public record BossBarAudience(BossBar bossBar, Audience audience, Duration time, Instant endOfBossBar, boolean timeChangesProgress) {

    public BossBarAudience(BossBar bossBar, Audience audience, BossBarNotification notification) {
        this(bossBar, audience, notification.time(), Instant.now().plus(notification.time()), notification.timeChangesProgress());
    }
}
