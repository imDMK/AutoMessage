package com.github.imdmk.automessage.command.implementation;

import com.github.imdmk.automessage.configuration.implementation.PluginConfiguration;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.settings.NotificationSettings;
import com.github.imdmk.automessage.text.Formatter;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Route(name = "automessage list")
public class AutoMessageListCommand {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public AutoMessageListCommand(PluginConfiguration pluginConfiguration, NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Execute
    void show(CommandSender sender) {
        List<Notification> autoMessages = this.pluginConfiguration.autoMessages;

        if (autoMessages.isEmpty()) {
            this.notificationSender.sendNotification(sender, this.notificationSettings.autoMessagesEmptyNotification);
            return;
        }

        this.notificationSender.sendNotification(sender, this.notificationSettings.autoMessagesListFirstNotification);

        boolean useHovers = sender instanceof Player && this.notificationSettings.autoMessagesListUseHover;

        AtomicInteger position = new AtomicInteger(0);

        for (Notification notification : autoMessages) {
            Formatter formatter = new Formatter()
                    .placeholder("{POSITION}", position.getAndIncrement())
                    .placeholder("{NOTIFICATION}", this.formatNotification(notification, useHovers));

            this.notificationSender.sendNotification(sender, this.notificationSettings.autoMessagesListNotification, formatter);
        }
    }

    private String formatNotification(Notification notification, boolean useHover) {
        return useHover ? this.formatNotificationHover(notification) : notification.format();
    }

    private String formatNotificationHover(Notification notification) {
        return "<hover:show_text:'" +  notification.format() + "'>" + notification.type();
    }
}
