package com.github.imdmk.automessage.command.implementation;

import com.github.imdmk.automessage.configuration.implementation.PluginConfiguration;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.settings.NotificationSettings;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;

@Route(name = "automessage remove")
@Permission("command.automessage.remove")
public class AutoMessageRemoveCommand {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public AutoMessageRemoveCommand(PluginConfiguration pluginConfiguration, NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Execute(required = 1)
    void remove(CommandSender sender, @Arg Notification notification) {
        this.pluginConfiguration.autoMessages.remove(notification);
        this.pluginConfiguration.save();

        this.notificationSender.sendNotification(sender, this.notificationSettings.autoMessageRemoved);
    }
}
