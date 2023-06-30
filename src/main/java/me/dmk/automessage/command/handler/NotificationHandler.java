package me.dmk.automessage.command.handler;

import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.Handler;
import me.dmk.automessage.notification.Notification;
import me.dmk.automessage.notification.NotificationSender;
import org.bukkit.command.CommandSender;

public class NotificationHandler implements Handler<CommandSender, Notification> {

    private final NotificationSender notificationSender;

    public NotificationHandler(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Notification notification) {
        this.notificationSender.sendMessage(sender, notification);
    }
}
