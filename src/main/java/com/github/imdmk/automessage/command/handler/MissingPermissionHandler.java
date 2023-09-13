package com.github.imdmk.automessage.command.handler;

import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

public class MissingPermissionHandler implements PermissionHandler<CommandSender> {

    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public MissingPermissionHandler(NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        Formatter formatter = new Formatter()
                .register("{PERMISSIONS}", requiredPermissions.getPermissions());

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.missingPermissionNotification, formatter);
    }
}
