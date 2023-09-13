package com.github.imdmk.automessage.command;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

@Route(name = "automessage send")
public class AutoMessageSendCommand {

    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public AutoMessageSendCommand(NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Execute(required = 2)
    void sendAutoMessage(CommandSender sender, @Arg Player target, @Arg Notification notification) {
        this.notificationSender.sendNotification(target, notification);

        Formatter formatter = new Formatter()
                .register("{PLAYER}", target.getName());

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessageSendNotification, formatter);
    }
}
