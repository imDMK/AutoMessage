package com.github.imdmk.automessage.command.handler;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationFormatter;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;

public class MissingPermissionHandler implements PermissionHandler<CommandSender> {

    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public MissingPermissionHandler(NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        Notification notification = new NotificationFormatter(this.notificationConfiguration.missingPermissionNotification)
                .placeholder("{PERMISSIONS}", requiredPermissions.getPermissions())
                .build();

        this.notificationSender.sendNotification(sender, notification);
    }
}
