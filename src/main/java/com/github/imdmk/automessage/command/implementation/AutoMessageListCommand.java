package com.github.imdmk.automessage.command.implementation;

import com.github.imdmk.automessage.configuration.implementation.PluginConfiguration;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.settings.NotificationSettings;
import com.github.imdmk.automessage.text.Formatter;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Route(name = "automessage list")
@Permission("command.automessage.list")
public class AutoMessageListCommand {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public AutoMessageListCommand(PluginConfiguration pluginConfiguration, NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Execute
    void show(CommandSender sender) {
        List<Notification> autoMessages = this.pluginConfiguration.autoMessages;

        if (autoMessages.isEmpty()) {
            this.notificationSender.sendNotification(sender, this.notificationSettings.autoMessagesEmpty);
            return;
        }

        this.notificationSender.sendNotification(sender, this.notificationSettings.autoMessagesListFirst);

        for (int i = 0; i < autoMessages.size(); i++) {
            Notification autoMessage = autoMessages.get(i);

            Formatter formatter = new Formatter()
                    .placeholder("{POSITION}", i + 1)
                    .placeholder("{NOTIFICATION}", this.formatNotification(sender, autoMessage));

            this.notificationSender.sendNotification(sender, this.notificationSettings.autoMessagesList, formatter);
        }
    }

    private String formatNotification(CommandSender sender, Notification notification) {
        return (sender instanceof Player) ? this.formatHover(notification) : notification.format();
    }

    private String formatHover(Notification notification) {
        return "<hover:show_text:'" +  notification.format() + "'>" + notification.type();
    }
}
