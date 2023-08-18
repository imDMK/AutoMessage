package com.github.imdmk.automessage.notification.implementation.bossbar;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;

import java.time.Duration;
import java.time.Instant;

public class BossBarTask implements Runnable {

    private final BossBarAudienceManager bossBarAudienceManager;

    public BossBarTask(BossBarAudienceManager bossBarAudienceManager) {
        this.bossBarAudienceManager = bossBarAudienceManager;
    }

    @Override
    public void run() {
        for (BossBarAudience bossBarAudience : this.bossBarAudienceManager.getBossBars()) {
            Audience audience = bossBarAudience.audience();
            BossBar bossBar = bossBarAudience.bossBar();

            Instant endOfBossBar = bossBarAudience.endOfBossBar();
            Instant now = Instant.now();

            if (now.isAfter(endOfBossBar)) {
                audience.hideBossBar(bossBar);
                this.bossBarAudienceManager.remove(bossBarAudience);
                return;
            }

            if (bossBarAudience.timeChangesProgress()) {
                Duration between = Duration.between(now, endOfBossBar);
                Duration divisor = Duration.ofNanos(endOfBossBar.getNano());

                float progress = between.dividedBy(divisor) / 10F;

                if (progress > 1.0F || progress < 0.F) {
                    bossBar.progress(progress > 1.0F ? BossBar.MAX_PROGRESS : BossBar.MIN_PROGRESS);
                    return;
                }

                bossBar.progress(progress);
            }
        }
    }
}
