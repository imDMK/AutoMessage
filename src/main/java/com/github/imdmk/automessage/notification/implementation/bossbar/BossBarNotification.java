package com.github.imdmk.automessage.notification.implementation.bossbar;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.util.ComponentUtil;
import com.github.imdmk.automessage.util.DurationUtil;
import com.github.imdmk.automessage.util.StringUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.time.Duration;

public record BossBarNotification(String name, Duration time, float progress, boolean timeChangesProgress, BossBar.Color color,
                                  BossBar.Overlay overlay) implements Notification {

    public BossBar create() {
        Component name = ComponentUtil.deserialize(this.name);
        return BossBar.bossBar(name, this.progress, this.color, this.overlay);
    }

    @Override
    public NotificationType type() {
        return NotificationType.BOSSBAR;
    }

    @Override
    public String format() {
        return StringUtil.GRAY_COLOR + this.type().name() + ":"
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "name: " + this.name
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "time: " + DurationUtil.toHumanReadable(this.time)
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "progress: " + this.progress
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "timeChangesProgress: " + this.timeChangesProgress
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "color: " + this.color.name()
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "overlay: " + this.overlay.name();
    }
}
