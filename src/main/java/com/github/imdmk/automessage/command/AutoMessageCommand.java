package com.github.imdmk.automessage.command;

import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;

import java.time.Duration;

@Route(name = "automessage")
public class AutoMessageCommand {

    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public AutoMessageCommand(NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Execute(route = "enable")
    public void enable(CommandSender sender) {
        this.notificationConfiguration.autoMessagesEnabled = true;

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesEnabledNotification);
    }

    @Execute(route = "disable")
    public void disable(CommandSender sender) {
        this.notificationConfiguration.autoMessagesEnabled = false;

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesDisabledNotification);
    }

    @Execute(route = "delay")
    public void delay(CommandSender sender, Duration delay) {
        this.notificationConfiguration.autoMessagesDelay = delay;

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesChangedDelayNotification);
    }
}
