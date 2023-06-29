package me.dmk.automessage.task;

import me.dmk.automessage.configuration.PluginConfiguration;
import me.dmk.automessage.notification.Notification;
import me.dmk.automessage.notification.NotificationSender;
import me.dmk.automessage.util.RandomUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoMessageTask implements Runnable {

    private final PluginConfiguration pluginConfiguration;

    private final AudienceProvider audienceProvider;
    private final NotificationSender notificationSender;

    private final AtomicInteger position = new AtomicInteger(0);

    public AutoMessageTask(PluginConfiguration pluginConfiguration, AudienceProvider audienceProvider, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.audienceProvider = audienceProvider;
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

        Optional<Notification> selectedNotificationOptional = this.selectNotification();
        if (selectedNotificationOptional.isEmpty()) {
            return;
        }

        Notification selectedNotification = selectedNotificationOptional.get();
        Audience audience = this.audienceProvider.players();

        this.notificationSender.sendMessage(audience, selectedNotification);
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
        List<Notification> notifications = this.pluginConfiguration.autoMessages;

        int position = this.position.getAndIncrement() % notifications.size();

        return notifications.stream()
                .skip(position)
                .findFirst();
    }
}
