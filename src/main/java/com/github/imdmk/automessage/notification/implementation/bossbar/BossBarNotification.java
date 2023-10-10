package com.github.imdmk.automessage.notification.implementation.bossbar;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.util.ComponentUtil;
import com.github.imdmk.automessage.util.DurationUtil;
import com.github.imdmk.automessage.util.StringUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.time.Duration;

public record BossBarNotification(String message, Duration time, float progress,
                                  BossBar.Color color, BossBar.Overlay overlay) implements Notification {

    public BossBar create() {
        Component name = ComponentUtil.deserialize(this.message);
        float replacedProgress = this.progress < 0.0F ? BossBar.MAX_PROGRESS : this.progress;

        return BossBar.bossBar(name, replacedProgress, this.color, this.overlay);
    }

    public BossBar create(Component name) {
        float replacedProgress = this.progress < 0.0F ? BossBar.MAX_PROGRESS : this.progress;

        return BossBar.bossBar(name, replacedProgress, this.color, this.overlay);
    }

    @Override
    public NotificationType type() {
        return NotificationType.BOSS_BAR;
    }

    @Override
    public String format() {
        return StringUtil.GRAY_COLOR + this.type().name() + ":"
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "name: " + this.message
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "time: " + DurationUtil.toHumanReadable(this.time)
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "progress: " + this.progress
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "color: " + this.color.name()
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "overlay: " + this.overlay.name();
    }
}
