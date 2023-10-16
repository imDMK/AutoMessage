package com.github.imdmk.automessage.command.implementation;

import com.github.imdmk.automessage.configuration.implementation.PluginConfiguration;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationSettings;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;

@Route(name = "automessage")
@Permission("command.automessage.switch.status")
public class AutoMessageStateCommand {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public AutoMessageStateCommand(PluginConfiguration pluginConfiguration, NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Execute(route = "enable")
    public void enable(CommandSender sender) {
        this.pluginConfiguration.autoMessagesEnabled = true;
        this.pluginConfiguration.save();

        this.notificationSender.sendNotification(sender, this.notificationSettings.autoMessagesEnabled);
    }

    @Execute(route = "disable")
    public void disable(CommandSender sender) {
        this.pluginConfiguration.autoMessagesEnabled = false;
        this.pluginConfiguration.save();

        this.notificationSender.sendNotification(sender, this.notificationSettings.autoMessagesDisabled);
    }
}
