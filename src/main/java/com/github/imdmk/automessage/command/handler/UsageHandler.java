package com.github.imdmk.automessage.command.handler;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationFormatter;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

public class UsageHandler implements InvalidUsageHandler<CommandSender> {

    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public UsageHandler(NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Schematic schematic) {
        if (schematic.isOnlyFirst()) {
            Notification notification = new NotificationFormatter(this.notificationConfiguration.invalidUsageNotification)
                    .placeholder("{USAGE}", schematic.first())
                    .build();

            this.notificationSender.sendNotification(sender, notification);
            return;
        }

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.invalidUsageFirstNotification);

        for (String schema : schematic.getSchematics()) {
            Notification notification = new NotificationFormatter(this.notificationConfiguration.invalidUsageListNotification)
                    .placeholder("{USAGE}", schema)
                    .build();

            this.notificationSender.sendNotification(sender, notification);
        }
    }
}
