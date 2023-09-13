package com.github.imdmk.automessage.command.handler;

import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

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
            Formatter formatter = new Formatter()
                    .register("{USAGE}", schematic.first());

            this.notificationSender.sendNotification(sender, this.notificationConfiguration.invalidUsageNotification, formatter);
            return;
        }

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.invalidUsageFirstNotification);

        for (String schema : schematic.getSchematics()) {
            Formatter formatter = new Formatter()
                    .register("{USAGE}", schema);

            this.notificationSender.sendNotification(sender, this.notificationConfiguration.invalidUsageListNotification, formatter);
        }
    }
}
