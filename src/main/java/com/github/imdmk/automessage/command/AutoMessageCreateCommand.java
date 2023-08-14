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
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.argument.joiner.Joiner;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.util.Arrays;

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

        this.notificationConfiguration.autoMessages.add(chatNotification);

        this.notificationSender.sendNotification(sender, this.createAddedNotification(chatNotification));
    }

    @Execute(route = "ACTIONBAR")
    void createActionbar(CommandSender sender, @Joiner @Name("message") String message) {
        ActionBarNotification actionBarNotification = new ActionBarNotification(message);

        this.notificationConfiguration.autoMessages.add(actionBarNotification);

        this.notificationSender.sendNotification(sender, this.createAddedNotification(actionBarNotification));
    }

    @Execute(route = "TITLE")
    void createTitle(CommandSender sender, @Joiner @Name("message") String message) {
        System.out.println(message);

        String[] splitMessage = message.split("\\|");

        System.out.println(Arrays.toString(splitMessage));

        if (splitMessage[0] == null || splitMessage[1] == null) {
            this.notificationSender.sendNotification(sender, this.notificationConfiguration.invalidTitleMessageNotification);
            return;
        }

        TitleNotification titleNotification = new TitleNotification(splitMessage[0], splitMessage[1]);

        this.notificationConfiguration.autoMessages.add(titleNotification);

        this.notificationSender.sendNotification(sender, this.createAddedNotification(titleNotification));
    }

    @Execute(route = "BOSSBAR")
    void createBossBar(CommandSender sender, @Arg @Name("time") Duration time, @Arg @Name("progress") float progress, @Arg @Name("color") BossBar.Color color, @Arg @Name("overlay") BossBar.Overlay overlay, @Joiner @Name("name") String name) {
        BossBarNotification bossBarNotification = new BossBarNotification(name, time, progress, color, overlay);

        this.notificationConfiguration.autoMessages.add(bossBarNotification);

        this.notificationSender.sendNotification(sender, this.createAddedNotification(bossBarNotification));
    }

    private Notification createAddedNotification(Notification notification) {
        return new NotificationFormatter(this.notificationConfiguration.autoMessageAddedNotification)
                .placeholder("{TYPE}", notification.type().name().toUpperCase())
                .placeholder("{POSITION}", this.notificationConfiguration.autoMessages.size() + 1)
                .build();
    }
}
