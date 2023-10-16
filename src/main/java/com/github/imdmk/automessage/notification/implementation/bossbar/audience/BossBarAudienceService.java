package com.github.imdmk.automessage.notification.implementation.bossbar.audience;

import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;

import java.time.Duration;
import java.time.Instant;

public class BossBarAudienceService {

    private final BossBarAudienceManager bossBarAudienceManager;

    public BossBarAudienceService(BossBarAudienceManager bossBarAudienceManager) {
        this.bossBarAudienceManager = bossBarAudienceManager;
    }

    public void update(BossBarAudience bossBarAudience) {
        BossBar bossBar = bossBarAudience.bossBar();

        Instant now = Instant.now();
        Instant endOfBossBar = bossBarAudience.endOfBossBar();

        if (now.isAfter(endOfBossBar)) {
            bossBarAudience.audience().hideBossBar(bossBar);
            this.bossBarAudienceManager.remove(bossBarAudience);
            return;
        }

        float audienceProgress = bossBarAudience.progress();

        if (audienceProgress < 0.0F) {
            Duration between = Duration.between(now, endOfBossBar);
            Duration audienceTime = bossBarAudience.time();

            float difference = (float) between.toMillis() / audienceTime.toMillis();
            float progress = Math.max(0.0F, Math.min(1.0F, difference));

            bossBar.progress(progress);
        }
    }

    public BossBarAudience create(Audience audience, BossBarNotification bossBarNotification) {
        BossBar bossBar = bossBarNotification.create();
        BossBarAudience bossBarAudience = new BossBarAudience(audience, bossBar, bossBarNotification);

        audience.showBossBar(bossBar);
        this.bossBarAudienceManager.add(bossBarAudience);

        return bossBarAudience;
    }
}
