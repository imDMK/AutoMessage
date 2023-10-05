package com.github.imdmk.automessage.notification.settings;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class NotificationSettings extends OkaeriConfig {

    @Comment("# Command notifications")
    public Notification autoMessagesEnabledNotification = new ChatNotification("<green>Enabled automatic messages<dark_gray>.");
    public Notification autoMessagesDisabledNotification = new ChatNotification("<red>Disabled automatic messages<dark_gray>.");

    @Comment("# {POSITION} - Position of automatic message")
    public Notification autoMessageAddedNotification = new ChatNotification("<green>Added new automatic message with type {TYPE} at position {POSITION}<dark_gray>.");
    public Notification autoMessageRemovedNotification = new ChatNotification("<red>Removed automatic message<dark_gray>.");

    public Notification autoMessageSendNotification = new ChatNotification("<red>An automatic message has been sent to the player {PLAYER}<dark_gray>.");

    @Comment({
            "# Format notification information in hovers?",
            "# More info at: https://docs.advntr.dev/minimessage/format.html#hover"
    })
    public boolean autoMessagesListUseHover = true;

    public Notification autoMessagesListFirstNotification = new ChatNotification("<gray>List of automatic messages<dark_gray>:");
    @Comment({
            "# {POSITION} - Position of automatic message",
            "# {NOTIFICATION} - Notification information"
    })
    public Notification autoMessagesListNotification = new ChatNotification("<gray>{POSITION}<dark_gray>. <red>{NOTIFICATION}");
    public Notification autoMessagesEmptyNotification = new ChatNotification("<red>Automatic messages is empty<dark_gray>.");

    public Notification autoMessageNotFoundNotification = new ChatNotification("<red>Automatic message not found<dark_gray>.");

    @Comment("# {USAGE} - The usage of command")
    public Notification invalidUsageNotification = new ChatNotification("<red>Invalid usage<dark_gray>: <red>{USAGE}");
    @Comment("# This notifications are used when there is more than one option to use a command")
    public Notification invalidUsageFirstNotification = new ChatNotification("<red>Invalid usage<dark_gray>:");
    public Notification invalidUsageListNotification = new ChatNotification("<dark_gray>- <red>{USAGE}");

    public Notification invalidNumberNotification = new ChatNotification("<red>Invalid number value<dark_gray>.");
    public Notification invalidFloatNotification = new ChatNotification("<red>Invalid float value<dark_gray>.");
    public Notification invalidTypeNotification = new ChatNotification("<red>Invalid notification type<dark_gray>.");
    public Notification missingPermissionNotification = new ChatNotification("<red>Missing permissions<dark_gray>: <red>{PERMISSIONS}");
    public Notification playerNotFoundNotification = new ChatNotification("<red>Player not found<dark_gray>.");

    public enum AutoMessageMode {
        RANDOM, SEQUENTIAL
    }
}
