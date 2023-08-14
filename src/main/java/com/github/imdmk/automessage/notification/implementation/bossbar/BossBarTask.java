package com.github.imdmk.automessage.notification.implementation.bossbar;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;

import java.time.Duration;
import java.time.Instant;

public class BossBarTask implements Runnable {

    private final AudienceProvider audienceProvider;
    private final BossBarManager bossBarManager;

    public BossBarTask(AudienceProvider audienceProvider, BossBarManager bossBarManager) {
        this.audienceProvider = audienceProvider;
        this.bossBarManager = bossBarManager;
    }

    @Override
    public void run() {
        this.bossBarManager.getBossBars().forEach((bossBar, instant) -> {
            Audience audience = this.audienceProvider.players();

            if (instant.isAfter(Instant.now())) {
                audience.hideBossBar(bossBar);
                return;
            }

            Duration between = Duration.between(Instant.now(), instant);

            float progress = (float) between.toMillis() / instant.getEpochSecond();

            bossBar.progress(progress);
        });
    }
}
