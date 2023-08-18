package com.github.imdmk.automessage.notification.configuration;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.implementation.ActionBarNotification;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import com.github.imdmk.automessage.notification.implementation.TitleNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import net.kyori.adventure.bossbar.BossBar;

import java.time.Duration;
import java.util.List;

public class NotificationConfiguration extends OkaeriConfig {

    @Comment({
            "# The value of whether automatic messages should be enabled",
            "# This value is changed when auto messages are disabled using the automessage enable/disable command"
    })
    public boolean autoMessagesEnabled = true;

    @Comment({
            "# How often should automatic messages be sent?",
            "# Example: 10s"
    })
    public Duration autoMessagesDelay = Duration.ofSeconds(10);

    @Comment({
            "# Automatic message sending mode",
            "# Modes: ",
            "# RANDOM: Automatic messages will be selected randomly",
            "# SEQUENTIAL: Automatic messages will be selected in turn"
    })
    public AutoMessageMode autoMessagesMode = AutoMessageMode.SEQUENTIAL;

    @Comment({
            "# Automatic message list",
            "# If you have trouble configuring the messages, please refer to the project's github."
    })
    public List<Notification> autoMessages = List.of(
            new ChatNotification("<red>This is first message"),
            new ActionBarNotification("<yellow>This is second message"),
            new TitleNotification("<yellow>Third message", "<red>This is third message"),
            new BossBarNotification("<red>Fourth message announcement!", Duration.ofSeconds(5), BossBar.MAX_PROGRESS, true, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
    );

    @Comment("# Command notifications")
    public Notification autoMessagesEnabledNotification = new ChatNotification("<green>Enabled automatic messages<dark_gray>.");
    public Notification autoMessagesDisabledNotification = new ChatNotification("<red>Disabled automatic messages<dark_gray>.");

    public Notification autoMessagesChangedDelayNotification = new ChatNotification("<green>Changed automatic messages delay<dark_gray>.");

    @Comment("# {POSITION} - Position of automatic message")
    public Notification autoMessageAddedNotification = new ChatNotification("<green>Added new automatic message with type {TYPE} at position {POSITION}<dark_gray>.");
    public Notification autoMessageRemovedNotification = new ChatNotification("<red>Removed automatic message<dark_gray>.");

    public Notification autoMessagesListFirstNotification = new ChatNotification("<gray>List of automatic messages<dark_gray>:");

    @Comment({
            "# {POSITION} - Position of automatic message",
            "# {TYPE} - The notification type of automatic message",
            "# {MESSAGE} - The content of automatic message"
    })
    public Notification autoMessagesListNotification = new ChatNotification("<gray>{POSITION}<dark_gray>. <dark_gray>: <gray>{NOTIFICATION}");

    public Notification autoMessagesEmptyNotification = new ChatNotification("<red>Automatic messages is empty<dark_gray>.");

    public Notification autoMessageNotFoundNotification = new ChatNotification("<red>Automatic message not found<dark_gray>.");

    public Notification invalidTitleMessageNotification = new ChatNotification("<red>Use the | character to separate title and subtitle messages<dark_gray>.\n<red>Example<dark_gray>: <red>This is title message | This is subtitle message<dark_gray>.");

    @Comment("# {USAGE} - The usage of command")
    public Notification invalidUsageNotification = new ChatNotification("<red>Invalid usage<dark_gray>: <red>{USAGE}");
    @Comment("# This notifications are used when there is more than one option to use a command")
    public Notification invalidUsageFirstNotification = new ChatNotification("<red>Invalid usage<dark_gray>:");
    public Notification invalidUsageListNotification = new ChatNotification("<dark_gray>- <red>{USAGE}");

    public Notification invalidNumberNotification = new ChatNotification("<red>Invalid number<dark_gray>.");
    public Notification invalidTypeNotification = new ChatNotification("<red>Invalid notification type<dark_gray>.");
    public Notification missingPermissionNotification = new ChatNotification("<red>Missing permissions<dark_gray>: <red>{PERMISSIONS}");

    public enum AutoMessageMode {
        RANDOM, SEQUENTIAL
    }
}
