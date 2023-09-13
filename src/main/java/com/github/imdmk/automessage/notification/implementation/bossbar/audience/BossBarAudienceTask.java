package com.github.imdmk.automessage.notification.implementation.bossbar.audience;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;

import java.time.Duration;
import java.time.Instant;

public class BossBarAudienceTask implements Runnable {

    private final BossBarAudienceManager bossBarAudienceManager;

    public BossBarAudienceTask(BossBarAudienceManager bossBarAudienceManager) {
        this.bossBarAudienceManager = bossBarAudienceManager;
    }

    @Override
    public void run() {
        for (BossBarAudience bossBarAudience : this.bossBarAudienceManager.getBossBarAudiences()) {
            Audience audience = bossBarAudience.audience();
            BossBar bossBar = bossBarAudience.bossBar();

            Instant now = Instant.now();
            Instant endOfBossBar = bossBarAudience.endOfBossBar();

            if (now.isAfter(endOfBossBar)) {
                audience.hideBossBar(bossBar);
                this.bossBarAudienceManager.remove(bossBarAudience);
                return;
            }

            if (bossBarAudience.timeChangesProgress()) {
                Duration between = Duration.between(now, endOfBossBar);
                Duration time = bossBarAudience.time();

                float difference = (float) between.toMillis() / time.toMillis();
                float progress = Math.max(0.0F, Math.min(1.0F, difference));

                bossBar.progress(progress);
            }
        }
    }
}
