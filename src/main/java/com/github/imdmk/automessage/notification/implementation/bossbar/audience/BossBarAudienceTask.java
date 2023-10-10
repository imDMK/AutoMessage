package com.github.imdmk.automessage.notification.implementation.bossbar.audience;

public class BossBarAudienceTask implements Runnable {

    private final BossBarAudienceManager bossBarAudienceManager;
    private final BossBarAudienceService bossBarAudienceService;

    public BossBarAudienceTask(BossBarAudienceManager bossBarAudienceManager, BossBarAudienceService bossBarAudienceService) {
        this.bossBarAudienceManager = bossBarAudienceManager;
        this.bossBarAudienceService = bossBarAudienceService;
    }

    @Override
    public void run() {
        for (BossBarAudience bossBarAudience : this.bossBarAudienceManager.getBossBarAudiences()) {
            this.bossBarAudienceService.update(bossBarAudience);
        }
    }
}
