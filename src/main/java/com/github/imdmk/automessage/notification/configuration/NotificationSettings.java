package com.github.imdmk.automessage.notification.configuration;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class NotificationSettings extends OkaeriConfig {

    @Comment("# Command notifications")
    public Notification autoMessagesEnabled = new ChatNotification("<green>Enabled automatic messages<dark_gray>.");
    public Notification autoMessagesDisabled = new ChatNotification("<red>Disabled automatic messages<dark_gray>.");

    @Comment("# {POSITION} - Position of automatic message")
    public Notification autoMessageAdded = new ChatNotification("<green>Added new automatic message with type {TYPE} at position {POSITION}<dark_gray>.");
    public Notification autoMessageRemoved = new ChatNotification("<red>Removed automatic message<dark_gray>.");

    public Notification autoMessagesListFirst = new ChatNotification("<gray>List of automatic messages<dark_gray>:");
    
    @Comment({
            "# {POSITION} - Position of automatic message",
            "# {NOTIFICATION} - Notification information"
    })
    public Notification autoMessagesList = new ChatNotification("<gray>{POSITION}<dark_gray>. <red>{NOTIFICATION}");
    
    public Notification autoMessagesEmpty = new ChatNotification("<red>Automatic messages is empty<dark_gray>.");

    public Notification autoMessageNotFound = new ChatNotification("<red>Automatic message not found<dark_gray>.");

    @Comment("# {USAGE} - The usage of command")
    public Notification invalidUsage = new ChatNotification("<red>Invalid usage<dark_gray>: <red>{USAGE}");
    
    @Comment("# Used when there is more than one option to use a command")
    public Notification invalidUsageListFirst = new ChatNotification("<red>Invalid usage<dark_gray>:");

    @Comment("# Used when there is more than one option to use a command")
    public Notification invalidUsageList = new ChatNotification("<dark_gray>- <red>{USAGE}");

    public Notification invalidNumber = new ChatNotification("<red>Invalid number value<dark_gray>.");
    public Notification invalidFloat = new ChatNotification("<red>Invalid float value<dark_gray>.");
    public Notification invalidNotificationType = new ChatNotification("<red>Invalid notification type<dark_gray>.");
    public Notification missingPermission = new ChatNotification("<red>Missing permissions<dark_gray>: <red>{PERMISSIONS}");
    public Notification playerNotFound = new ChatNotification("<red>Player not found<dark_gray>.");
}
