package com.github.imdmk.automessage.command;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationFormatter;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Route(name = "automessage send")
public class AutoMessageSendCommand {

    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public AutoMessageSendCommand(NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Execute(required = 1)
    void sendAutoMessage(CommandSender sender, @Arg Player target, @Arg Notification notification) {
        this.notificationSender.sendNotification(target, notification);

        Notification autoMessageSendNotification = new NotificationFormatter(this.notificationConfiguration.autoMessageSendNotification)
                .placeholder("{PLAYER}", target.getName())
                .build();

        this.notificationSender.sendNotification(sender, autoMessageSendNotification);
    }
}
