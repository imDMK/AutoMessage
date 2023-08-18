package com.github.imdmk.automessage.notification.implementation.bossbar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BossBarAudienceManager {

    private final HashSet<BossBarAudience> bossBars = new HashSet<>();

    public void add(BossBarAudience bossBarAudience) {
        this.bossBars.add(bossBarAudience);
    }

    public void remove(BossBarAudience bossBarAudience) {
        this.bossBars.remove(bossBarAudience);
    }

    public Set<BossBarAudience> getBossBars() {
        return Collections.unmodifiableSet(this.bossBars);
    }
}
