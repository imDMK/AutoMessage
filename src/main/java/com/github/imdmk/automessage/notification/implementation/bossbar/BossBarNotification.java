package com.github.imdmk.automessage.notification.implementation.bossbar;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.util.ComponentUtil;
import com.github.imdmk.automessage.util.DurationUtil;
import net.kyori.adventure.bossbar.BossBar;

import java.time.Duration;

public record BossBarNotification(String name, Duration time, float progress, boolean timeChangesProgress, BossBar.Color color,
                                  BossBar.Overlay overlay) implements Notification {

    public BossBar create() {
        return BossBar.bossBar(ComponentUtil.deserialize(this.name), this.progress, this.color, this.overlay);
    }

    @Override
    public NotificationType type() {
        return NotificationType.BOSSBAR;
    }

    @Override
    public String format() {
        return this.type().name()
                + ", name: " + this.name
                + ", time: " + DurationUtil.toHumanReadable(this.time)
                + ", progress: " + this.progress
                + ", timeChangesProgress: " + this.timeChangesProgress
                + ", color: " + this.color.name().toUpperCase()
                + ", overlay: " + this.overlay.name().toUpperCase();
    }
}
