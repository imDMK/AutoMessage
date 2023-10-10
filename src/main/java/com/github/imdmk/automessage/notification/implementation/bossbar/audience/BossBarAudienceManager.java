package com.github.imdmk.automessage.notification.implementation.bossbar.audience;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BossBarAudienceManager {

    private final Set<BossBarAudience> bossBarAudiences = new HashSet<>();

    public void add(BossBarAudience bossBarAudience) {
        this.bossBarAudiences.add(bossBarAudience);
    }

    public void remove(BossBarAudience bossBarAudience) {
        this.bossBarAudiences.remove(bossBarAudience);
    }

    public Set<BossBarAudience> getBossBarAudiences() {
        return Collections.unmodifiableSet(this.bossBarAudiences);
    }
}
