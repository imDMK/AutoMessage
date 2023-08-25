package com.github.imdmk.automessage.command;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationFormatter;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Route(name = "automessage")
public class AutoMessageListCommand {

    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public AutoMessageListCommand(NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Execute(route = "list")
    void listAutoMessages(CommandSender sender) {
        List<Notification> autoMessages = this.notificationConfiguration.autoMessages;

        if (autoMessages.isEmpty()) {
            this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesEmptyNotification);
            return;
        }

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesListFirstNotification);

        boolean useHovers = sender instanceof Player && this.notificationConfiguration.autoMessagesListUseHover;

        AtomicInteger position = new AtomicInteger(0);

        for (Notification notification : autoMessages) {
            Notification autoMessagesListNotification = new NotificationFormatter(this.notificationConfiguration.autoMessagesListNotification)
                    .placeholder("{POSITION}", position.getAndIncrement())
                    .placeholder("{NOTIFICATION}", this.formatNotification(notification, useHovers))
                    .build();

            this.notificationSender.sendNotification(sender, autoMessagesListNotification);
        }
    }

    private String formatNotification(Notification notification, boolean useHover) {
        return useHover ? this.formatNotificationHover(notification) : notification.format();
    }

    private String formatNotificationHover(Notification notification) {
        return "<hover:show_text:'" +  notification.format() + "'>" + notification.type();
    }
}
