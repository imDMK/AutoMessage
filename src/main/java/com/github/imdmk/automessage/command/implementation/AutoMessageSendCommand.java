package com.github.imdmk.automessage.command.implementation;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.settings.NotificationSettings;
import com.github.imdmk.automessage.text.Formatter;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Route(name = "automessage send")
public class AutoMessageSendCommand {

    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public AutoMessageSendCommand(NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Execute(required = 2)
    void send(CommandSender sender, @Arg Player target, @Arg Notification notification) {
        this.notificationSender.sendNotification(target, notification);

        Formatter formatter = new Formatter()
                .placeholder("{PLAYER}", target.getName());

        this.notificationSender.sendNotification(sender, this.notificationSettings.autoMessageSendNotification, formatter);
    }
}
