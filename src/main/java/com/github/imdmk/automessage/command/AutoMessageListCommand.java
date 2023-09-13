package com.github.imdmk.automessage.command;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Route(name = "automessage list")
public class AutoMessageListCommand {

    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public AutoMessageListCommand(NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Execute
    void showAutoMessages(CommandSender sender) {
        List<Notification> autoMessages = this.notificationConfiguration.autoMessages;

        if (autoMessages.isEmpty()) {
            this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesEmptyNotification);
            return;
        }

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesListFirstNotification);

        boolean useHovers = sender instanceof Player && this.notificationConfiguration.autoMessagesListUseHover;

        AtomicInteger position = new AtomicInteger(0);

        for (Notification notification : autoMessages) {
            Formatter formatter = new Formatter()
                    .register("{POSITION}", position.getAndIncrement())
                    .register("{NOTIFICATION}", this.formatNotification(notification, useHovers));

            this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesListNotification, formatter);
        }
    }

    private String formatNotification(Notification notification, boolean useHover) {
        return useHover ? this.formatNotificationHover(notification) : notification.format();
    }

    private String formatNotificationHover(Notification notification) {
        return "<hover:show_text:'" +  notification.format() + "'>" + notification.type();
    }
}
