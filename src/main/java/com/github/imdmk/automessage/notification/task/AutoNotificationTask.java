package com.github.imdmk.automessage.notification.task;

import com.github.imdmk.automessage.configuration.implementation.PluginConfiguration;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.audience.BossBarAudienceService;
import com.github.imdmk.automessage.notification.settings.NotificationSettings;
import com.github.imdmk.automessage.util.CollectionUtil;
import net.kyori.adventure.audience.Audience;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoNotificationTask implements Runnable {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSender notificationSender;
    private final BossBarAudienceService bossBarAudienceService;

    private final AtomicInteger position = new AtomicInteger(0);

    public AutoNotificationTask(PluginConfiguration pluginConfiguration, NotificationSender notificationSender, BossBarAudienceService bossBarAudienceService) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSender = notificationSender;
        this.bossBarAudienceService = bossBarAudienceService;
    }

    @Override
    public void run() {
        if (!this.pluginConfiguration.autoMessagesEnabled) {
            return;
        }

        if (this.pluginConfiguration.autoMessages.isEmpty()) {
            return;
        }

        Audience audience = this.notificationSender.audiencePlayers();
        Notification notification = this.selectNotification();

        this.notificationSender.sendNotification(audience, notification);

        if (notification instanceof BossBarNotification bossBarNotification) {
            this.bossBarAudienceService.create(audience, bossBarNotification);
        }
    }

    private Notification selectNotification() {
        NotificationSettings.AutoMessageMode autoMessageMode = this.pluginConfiguration.autoMessagesMode;
        List<Notification> autoMessages = this.pluginConfiguration.autoMessages;

        return switch (autoMessageMode) {
            case RANDOM -> this.selectRandomNotification(autoMessages);
            case SEQUENTIAL -> this.selectNextNotification(autoMessages);
        };
    }

    private Notification selectRandomNotification(List<Notification> notifications) {
        return CollectionUtil.getRandom(notifications).orElseThrow();
    }

    private Notification selectNextNotification(List<Notification> notifications) {
        int position = this.position.getAndIncrement() % notifications.size();

        return CollectionUtil.select(notifications, position).orElseThrow();
    }
}
