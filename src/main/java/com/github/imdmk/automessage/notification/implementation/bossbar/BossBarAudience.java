package com.github.imdmk.automessage.notification.implementation.bossbar;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;

import java.time.Instant;

public record BossBarAudience(BossBar bossBar, Audience audience, Instant endOfBossBar, boolean timeChangesProgress) {
}
