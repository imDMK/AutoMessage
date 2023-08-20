package com.github.imdmk.automessage.command;

import com.github.imdmk.automessage.configuration.PluginConfiguration;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;

import java.time.Duration;

@Route(name = "automessage")
public class AutoMessageCommand {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public AutoMessageCommand(PluginConfiguration pluginConfiguration, NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Execute(route = "enable")
    public void enable(CommandSender sender) {
        this.notificationConfiguration.autoMessagesEnabled = true;
        this.pluginConfiguration.save();

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesEnabledNotification);
    }

    @Execute(route = "disable")
    public void disable(CommandSender sender) {
        this.notificationConfiguration.autoMessagesEnabled = false;
        this.pluginConfiguration.save();

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesDisabledNotification);
    }

    @Execute(route = "delay")
    public void delay(CommandSender sender, @Arg @Name("delay") Duration delay) {
        this.notificationConfiguration.autoMessagesDelay = delay;
        this.pluginConfiguration.save();

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessagesChangedDelayNotification);
    }
}
