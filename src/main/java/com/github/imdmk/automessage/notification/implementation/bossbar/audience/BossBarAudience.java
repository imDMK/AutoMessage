package com.github.imdmk.automessage.notification.implementation.bossbar.audience;

import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;

import java.time.Duration;
import java.time.Instant;

public record BossBarAudience(Audience audience, BossBar bossBar, Instant endOfBossBar, Duration time, float progress) {

    public BossBarAudience(Audience audience, BossBar bossBar, BossBarNotification notification) {
        this(audience, bossBar, Instant.now().plus(notification.time()), notification.time(), notification.progress());
    }
}
