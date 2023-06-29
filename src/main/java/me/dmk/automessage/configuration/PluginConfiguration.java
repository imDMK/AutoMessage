package me.dmk.automessage.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import me.dmk.automessage.notification.Notification;
import me.dmk.automessage.notification.NotificationType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Header({
        "#",
        "# Configuration file for auto message plugin",
        "# If you have any question visit project's github",
        "#"
})
public class PluginConfiguration extends OkaeriConfig {

    @Comment("# Booleans")
    public boolean checkForUpdates = true;

    public boolean autoMessagesEnabled = true;

    @Comment("# Auto message settings")
    public Duration autoMessagesDelay = Duration.ofSeconds(5);

    @Comment({
            "# Auto message mode",
            "# Modes: RANDOM, SEQUENTIAL"
    })
    public AutoMessageMode autoMessageMode = AutoMessageMode.SEQUENTIAL;

    public List<Notification> autoMessages = new ArrayList<>(List.of(
            new Notification(NotificationType.CHAT, "This is first auto message!"),
            new Notification(NotificationType.ACTIONBAR, "This is second auto message!")
    ));

    @Comment("# Command section")
    public boolean autoMessageCommandEnabled = true;
    public String autoMessageCommandPermission = "command.automessage";

    @Comment("# Messages")
    @Comment("# Argument messages")
    public Notification invalidNumberMessage = new Notification(NotificationType.CHAT, "<red>Invalid number.");
    public Notification invalidNotificationTypeMessage = new Notification(NotificationType.CHAT, "<red>Invalid notification type.");
    public Notification missingPermissionsMessage = new Notification(NotificationType.CHAT, "<red>Missing permissions: <dark_red>{PERMISSIONS}");

    public Notification notificationNotFoundMessage = new Notification(NotificationType.CHAT, "<red>Notification at <dark_red>{POSITION} <red>position not found.");
    public Notification notificationListEmptyMessage = new Notification(NotificationType.CHAT, "<red>No automatic messages.");

    public Notification invalidUsageMessage = new Notification(NotificationType.CHAT, "<red>Invalid usage: <dark_red>{USAGE}");
    @Comment("# This messages is used when there is more than one option to use a command")
    public Notification invalidUsageFirstMessage = new Notification(NotificationType.CHAT, "<red>Invalid usage:");
    public Notification invalidUsageListMessage = new Notification(NotificationType.CHAT, "<dark_gray>- <red>{USAGE}");

    @Comment("# Command messages")
    public Notification autoMessagesEnabledMessage = new Notification(NotificationType.CHAT, "<green>Enabled sending auto messages.");
    public Notification autoMessagesDisabledMessage = new Notification(NotificationType.CHAT, "<red>Disabled sending auto messages.");

    public Notification addedNotificationMessage = new Notification(NotificationType.CHAT, "<green>Added notification.");
    public Notification removedNotificationMessage = new Notification(NotificationType.CHAT, "<red>Removed notification.");

    public Notification listNotificationFirstMessage = new Notification(NotificationType.CHAT, "<gray>List of auto messages:");
    public Notification listNotificationMessage = new Notification(NotificationType.CHAT, "<gray>{POSITION}. <green>{TYPE}<dark_gray>: <green>{MESSAGE}");

    public enum AutoMessageMode {
        RANDOM, SEQUENTIAL
    }
}
