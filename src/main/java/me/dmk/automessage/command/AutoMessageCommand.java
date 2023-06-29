package me.dmk.automessage.command;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.argument.joiner.Joiner;
import dev.rollczi.litecommands.command.async.Async;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import me.dmk.automessage.configuration.PluginConfiguration;
import me.dmk.automessage.notification.Notification;
import me.dmk.automessage.notification.NotificationSender;
import me.dmk.automessage.notification.NotificationType;
import org.bukkit.entity.Player;

import java.util.List;

@Route(name = "automessage")
public class AutoMessageCommand {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSender notificationSender;

    public AutoMessageCommand(PluginConfiguration pluginConfiguration, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSender = notificationSender;
    }

    @Async
    @Execute(route = "enable")
    void enable(Player player) {
        this.pluginConfiguration.autoMessagesEnabled = true;

        this.notificationSender.sendMessage(player, this.pluginConfiguration.autoMessagesEnabledMessage);
    }

    @Async
    @Execute(route = "disable")
    void disable(Player player) {
        this.pluginConfiguration.autoMessagesEnabled = false;

        this.notificationSender.sendMessage(player, this.pluginConfiguration.autoMessagesDisabledMessage);
    }

    @Async
    @Execute(route = "add-notification")
    void addNotification(Player player, @Arg NotificationType notificationType, @Joiner @Name("message") String message) {
        Notification notification = new Notification(notificationType, message);

        this.pluginConfiguration.autoMessages.add(notification);

        this.notificationSender.sendMessage(player, this.pluginConfiguration.addedNotificationMessage);
    }

    @Async
    @Execute(route = "list-notification")
    void listNotification(Player player) {
        List<Notification> notifications = this.pluginConfiguration.autoMessages;

        if (notifications.isEmpty()) {
            this.notificationSender.sendMessage(player, this.pluginConfiguration.notificationListEmptyMessage);
            return;
        }

        this.notificationSender.sendMessage(player, this.pluginConfiguration.listNotificationFirstMessage);

        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);

            this.notificationSender.builder()
                    .fromNotification(this.pluginConfiguration.listNotificationMessage)
                    .placeholder("{POSITION}", i)
                    .placeholder("{TYPE}", notification.getType())
                    .placeholder("{MESSAGE}", notification.getMessage())
                    .send(player);
        }
    }

    @Async
    @Execute(route = "remove-notification")
    void removeNotification(Player player, @Arg Notification notification) {
        this.pluginConfiguration.autoMessages.remove(notification);

        this.notificationSender.sendMessage(player, this.pluginConfiguration.removedNotificationMessage);
    }
}
