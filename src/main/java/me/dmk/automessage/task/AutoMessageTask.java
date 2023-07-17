package me.dmk.automessage.task;

import me.dmk.automessage.configuration.PluginConfiguration;
import me.dmk.automessage.notification.Notification;
import me.dmk.automessage.notification.NotificationSender;
import me.dmk.automessage.util.RandomUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoMessageTask implements Runnable {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSender notificationSender;

    private final AtomicInteger position = new AtomicInteger(0);

    public AutoMessageTask(PluginConfiguration pluginConfiguration, NotificationSender notificationSender) {
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
        return switch (this.pluginConfiguration.autoMessageMode) {
            case RANDOM -> this.selectRandomNotification();
            case SEQUENTIAL -> this.selectNextNotification();
        };
    }

    private Optional<Notification> selectRandomNotification() {
        return RandomUtil.getRandom(this.pluginConfiguration.autoMessages);
    }

    private Optional<Notification> selectNextNotification() {
        List<Notification> autoMessages = this.pluginConfiguration.autoMessages;

        int position = this.position.getAndIncrement() % autoMessages.size();

        return autoMessages.stream()
                .skip(position)
                .findFirst();
    }
}
