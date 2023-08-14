package com.github.imdmk.automessage.command.handler;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.Handler;
import org.bukkit.command.CommandSender;

public class NotificationHandler implements Handler<CommandSender, Notification> {

    private final NotificationSender notificationSender;

    public NotificationHandler(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Notification notification) {
        this.notificationSender.sendNotification(sender, notification);
    }
}
