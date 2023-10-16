package com.github.imdmk.automessage.configuration.implementation;

import com.github.imdmk.automessage.command.settings.CommandSettings;
import com.github.imdmk.automessage.mode.AutoMessageMode;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.implementation.ActionBarNotification;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import com.github.imdmk.automessage.notification.implementation.SubTitleNotification;
import com.github.imdmk.automessage.notification.implementation.TitleNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import com.github.imdmk.automessage.notification.settings.NotificationSettings;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import net.kyori.adventure.bossbar.BossBar;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class PluginConfiguration extends OkaeriConfig {

    @Comment({
            "# Specifies whether to check for a new plugin version when the administrator joins the server",
            "# I highly recommend enabling this option"
    })
    public boolean checkForUpdate = true;

    @Comment({
            "# Specifies whether automatic messages should be turned on",
            "# Note: This value changes when you use the automessage enable/disable command"
    })
    public boolean autoMessagesEnabled = true;

    @Comment("# How often should automatic messages be sent?")
    public Duration autoMessagesDelay = Duration.ofSeconds(10);

    @Comment({
            "# Automatic messaging mode",
            "# Modes:",
            "# RANDOM - Messages will be selected randomly",
            "# SEQUENTIAL - The messages will be selected one by one"
    })
    public AutoMessageMode autoMessagesMode = AutoMessageMode.SEQUENTIAL;

    @Comment("# Automatic messages")
    public List<Notification> autoMessages = Arrays.asList(
            new ChatNotification("<red>This is first message"),
            new ActionBarNotification("<yellow>This is second message"),
            new TitleNotification("<yellow>Third message"),
            new SubTitleNotification("<blue>Fourth message"),
            new BossBarNotification("<red>Five message announcement!", Duration.ofSeconds(5), BossBar.MAX_PROGRESS, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
    );

    @Comment({"#", "# Command configuration", "#"})
    public CommandSettings commandSettings = new CommandSettings();

    @Comment({"#", "# Notification settings", "#"})
    public NotificationSettings notificationSettings = new NotificationSettings();

}
