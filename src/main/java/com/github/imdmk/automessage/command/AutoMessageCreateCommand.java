package com.github.imdmk.automessage.command;

import com.github.imdmk.automessage.configuration.PluginConfiguration;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import com.github.imdmk.automessage.notification.implementation.ActionBarNotification;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import com.github.imdmk.automessage.notification.implementation.SubTitleNotification;
import com.github.imdmk.automessage.notification.implementation.TitleNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.By;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.argument.joiner.Joiner;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import dev.rollczi.litecommands.suggestion.Suggest;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

import java.time.Duration;

@Route(name = "automessage create")
public class AutoMessageCreateCommand {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationConfiguration notificationConfiguration;
    private final NotificationSender notificationSender;

    public AutoMessageCreateCommand(PluginConfiguration pluginConfiguration, NotificationConfiguration notificationConfiguration, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationConfiguration = notificationConfiguration;
        this.notificationSender = notificationSender;
    }

    @Execute(route = "CHAT", min = 1)
    void createChat(CommandSender sender, @Joiner @Suggest("<red>Example chat message") @Name("message") String message) {
        ChatNotification chatNotification = new ChatNotification(message);

        this.addAutoNotification(chatNotification);
        this.sendAddedNotification(sender, chatNotification);
    }

    @Execute(route = "ACTIONBAR", min = 1)
    void createActionbar(CommandSender sender, @Joiner @Suggest("<green>Example actionbar message") @Name("message") String message) {
        ActionBarNotification actionBarNotification = new ActionBarNotification(message);

        this.addAutoNotification(actionBarNotification);
        this.sendAddedNotification(sender, actionBarNotification);
    }

    @Execute(route = "TITLE", min = 1)
    void createTitle(CommandSender sender, @Joiner @Suggest("<yellow>Example title message") @Name("message") String message) {
        TitleNotification titleNotification = new TitleNotification(message);

        this.addAutoNotification(titleNotification);
        this.sendAddedNotification(sender, titleNotification);
    }

    @Execute(route = "SUB_TITLE", min = 1)
    void createSubTitle(CommandSender sender, @Joiner @Suggest("<yellow>Example subtitle message") @Name("message") String message) {
        SubTitleNotification subTitleNotification = new SubTitleNotification(message);

        this.addAutoNotification(subTitleNotification);
        this.sendAddedNotification(sender, subTitleNotification);
    }

    @Execute(route = "BOSS_BAR", min = 6)
    void createBossBar(CommandSender sender,
                       @Arg @Name("time") Duration time,
                       @Arg @By("bossBarProgress") float progress,
                       @Arg @Name("timeChangesProgress") boolean timeChangesProgress,
                       @Arg @Name("color") BossBar.Color color,
                       @Arg @Name("overlay") BossBar.Overlay overlay,
                       @Joiner @Suggest("<red>Example name") @Name("name") String name) {
        BossBarNotification bossBarNotification = new BossBarNotification(name, time, progress, timeChangesProgress, color, overlay);

        this.addAutoNotification(bossBarNotification);
        this.sendAddedNotification(sender, bossBarNotification);
    }

    private void addAutoNotification(Notification notification) {
        this.notificationConfiguration.autoMessages.add(notification);
        this.pluginConfiguration.save();
    }

    private void sendAddedNotification(CommandSender sender, Notification addedNotification) {
        Formatter formatter = new Formatter()
                .register("{TYPE}", addedNotification.type().name())
                .register("{POSITION}", this.notificationConfiguration.autoMessages.size() - 1);

        this.notificationSender.sendNotification(sender, this.notificationConfiguration.autoMessageAddedNotification, formatter);
    }
}
