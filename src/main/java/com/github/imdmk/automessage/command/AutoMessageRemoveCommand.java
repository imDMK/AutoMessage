package com.github.imdmk.automessage.command;

import com.github.imdmk.automessage.configuration.PluginConfiguration;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;

@Route(name = "automessage remove")
public class AutoMessageRemoveCommand {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public AutoMessageRemoveCommand(PluginConfiguration pluginConfiguration, NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Execute(required = 1)
    void remove(CommandSender sender, @Arg Notification notification) {
        this.notificationConfiguration.autoMessages.remove(notification);
        this.pluginConfiguration.save();

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessageRemovedNotification);
    }
}
