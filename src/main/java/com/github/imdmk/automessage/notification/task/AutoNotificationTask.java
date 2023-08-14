package com.github.imdmk.automessage.notification.task;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import com.github.imdmk.automessage.util.RandomUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoNotificationTask implements Runnable {

    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    private final AtomicInteger position = new AtomicInteger(0);

    public AutoNotificationTask(NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Override
    public void run() {
        if (!this.notificationConfiguration.autoMessagesEnabled) {
            return;
        }

        if (this.notificationConfiguration.autoMessages.isEmpty()) {
            return;
        }

        this.selectNotification().ifPresent(this.notificationSender::broadcast);
    }

    private Optional<Notification> selectNotification() {
        return switch (this.notificationConfiguration.autoMessagesMode) {
            case RANDOM -> this.selectRandomNotification();
            case SEQUENTIAL -> this.selectNextNotification();
        };
    }

    private Optional<Notification> selectRandomNotification() {
        return RandomUtil.getRandom(this.notificationConfiguration.autoMessages);
    }

    private Optional<Notification> selectNextNotification() {
        List<Notification> autoMessages = this.notificationConfiguration.autoMessages;

        int position = this.position.getAndIncrement() % autoMessages.size();

        return autoMessages.stream()
                .skip(position)
                .findFirst();
    }
}
