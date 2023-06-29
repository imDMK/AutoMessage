package me.dmk.automessage.command.handler;

import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import me.dmk.automessage.configuration.PluginConfiguration;
import me.dmk.automessage.notification.NotificationSender;
import org.bukkit.command.CommandSender;

public class PermissionHandler implements dev.rollczi.litecommands.handle.PermissionHandler<CommandSender> {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSender notificationSender;

    public PermissionHandler(PluginConfiguration pluginConfiguration, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        this.notificationSender.builder()
                .fromNotification(this.pluginConfiguration.missingPermissionsMessage)
                .placeholder("{PERMISSIONS}", requiredPermissions.getPermissions())
                .send(sender);
    }
}
