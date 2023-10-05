package com.github.imdmk.automessage.notification.task;

import com.github.imdmk.automessage.configuration.implementation.PluginConfiguration;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.settings.NotificationSettings;
import com.github.imdmk.automessage.util.CollectionUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoNotificationTask implements Runnable {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSender notificationSender;

    private final AtomicInteger position = new AtomicInteger(0);

    public AutoNotificationTask(PluginConfiguration pluginConfiguration, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSender = notificationSender;
    }

    @Override
    public void run() {
        if (!this.pluginConfiguration.autoMessagesEnabled) {
            return;
        }

        if (this.pluginConfiguration.autoMessages.isEmpty()) {
            return;
        }

        this.selectNotification().ifPresent(this.notificationSender::broadcast);
    }

    private Optional<Notification> selectNotification() {
        NotificationSettings.AutoMessageMode autoMessageMode = this.pluginConfiguration.autoMessagesMode;
        List<Notification> autoMessages = this.pluginConfiguration.autoMessages;

        return switch (autoMessageMode) {
            case RANDOM -> this.selectRandomNotification(autoMessages);
            case SEQUENTIAL -> this.selectNextNotification(autoMessages);
        };
    }

    private Optional<Notification> selectRandomNotification(List<Notification> notifications) {
        return CollectionUtil.getRandom(notifications);
    }

    private Optional<Notification> selectNextNotification(List<Notification> notifications) {
        int position = this.position.getAndIncrement() % notifications.size();

        return CollectionUtil.select(notifications, position);
    }
}
