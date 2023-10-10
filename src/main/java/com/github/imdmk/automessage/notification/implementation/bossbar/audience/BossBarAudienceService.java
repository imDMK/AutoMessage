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
            this.hide(bossBarAudience);
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

    public BossBarAudience create(Audience audience, BossBarNotification notification) {
        BossBar bossBar = notification.create();
        BossBarAudience bossBarAudience = new BossBarAudience(audience, bossBar, notification);

        this.bossBarAudienceManager.add(bossBarAudience);
        return bossBarAudience;
    }

    public void hide(BossBarAudience bossBarAudience) {
        Audience audience = bossBarAudience.audience();
        BossBar bossBar = bossBarAudience.bossBar();

        audience.hideBossBar(bossBar);
        this.bossBarAudienceManager.remove(bossBarAudience);
    }
}
