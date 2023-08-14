package com.github.imdmk.automessage.notification.implementation.bossbar;

import net.kyori.adventure.bossbar.BossBar;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager {

    private final Map<BossBar, Instant> bossBars = new ConcurrentHashMap<>();

    public void add(BossBar bossBar, Duration time) {
        this.bossBars.put(bossBar, Instant.now().plus(time));
    }

    public void remove(BossBar bossBar) {
        this.bossBars.remove(bossBar);
    }

    public Map<BossBar, Instant> getBossBars() {
        return this.bossBars;
    }
}
