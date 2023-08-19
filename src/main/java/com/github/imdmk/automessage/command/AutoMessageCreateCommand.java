package com.github.imdmk.automessage.command;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationFormatter;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import com.github.imdmk.automessage.notification.implementation.ActionBarNotification;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import com.github.imdmk.automessage.notification.implementation.TitleNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.By;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.argument.joiner.Joiner;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.command.CommandSender;

import java.time.Duration;

@Route(name = "automessage create")
public class AutoMessageCreateCommand {

    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public AutoMessageCreateCommand(NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Execute(route = "CHAT")
    void createChat(CommandSender sender, @Joiner  @Name("message") String message) {
        ChatNotification chatNotification = new ChatNotification(message);

        this.addAutoNotification(chatNotification);
        this.sendAddedNotification(sender, chatNotification);
    }

    @Execute(route = "ACTIONBAR")
    void createActionbar(CommandSender sender, @Joiner @Name("message") String message) {
        ActionBarNotification actionBarNotification = new ActionBarNotification(message);

        this.addAutoNotification(actionBarNotification);
        this.sendAddedNotification(sender, actionBarNotification);
    }

    @Execute(route = "TITLE")
    void createTitle(CommandSender sender, @Joiner @Name("message") String message) {
        String[] splitMessage = message.split("\\|");

        if (splitMessage.length < 2) {
            this.notificationSender.sendNotification(sender, this.notificationConfiguration.invalidTitleMessageNotification);
            return;
        }

        TitleNotification titleNotification = new TitleNotification(splitMessage[0], splitMessage[1]);

        this.addAutoNotification(titleNotification);
        this.sendAddedNotification(sender, titleNotification);
    }

    @Execute(route = "BOSSBAR")
    void createBossBar(CommandSender sender, @Arg @Name("time") Duration time, @Arg @By("bossBarProgress") @Name("progress") float progress, @Arg @Name("timeChangesProgress") boolean timeChangesProgress, @Arg @Name("color") BossBar.Color color, @Arg @Name("overlay") BossBar.Overlay overlay, @Joiner @Name("name") String name) {
        BossBarNotification bossBarNotification = new BossBarNotification(name, time, progress, timeChangesProgress, color, overlay);

        this.addAutoNotification(bossBarNotification);
        this.sendAddedNotification(sender, bossBarNotification);
    }

    private void addAutoNotification(Notification notification) {
        this.notificationConfiguration.autoMessages.add(notification);
    }

    private void sendAddedNotification(CommandSender sender, Notification addedNotification) {
        Notification notification = new NotificationFormatter(this.notificationConfiguration.autoMessageAddedNotification)
                .placeholder("{TYPE}", addedNotification.type().name().toUpperCase())
                .placeholder("{POSITION}", this.notificationConfiguration.autoMessages.size())
                .build();

        this.notificationSender.sendNotification(sender, notification);
    }
}
