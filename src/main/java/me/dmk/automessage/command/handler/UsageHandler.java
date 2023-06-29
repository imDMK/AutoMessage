package me.dmk.automessage.command.handler;

import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import me.dmk.automessage.configuration.PluginConfiguration;
import me.dmk.automessage.notification.NotificationSender;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UsageHandler implements InvalidUsageHandler<CommandSender> {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSender notificationSender;

    public UsageHandler(PluginConfiguration pluginConfiguration, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Schematic schematic) {
        List<String> schematics = schematic.getSchematics();

        if (schematics.size() == 1) {
            this.notificationSender.builder()
                    .fromNotification(this.pluginConfiguration.invalidUsageMessage)
                    .placeholder("{USAGE}", schematics.get(0))
                    .send(sender);
            return;
        }

        this.notificationSender.sendMessage(sender, this.pluginConfiguration.invalidUsageFirstMessage);

        for (String usage : schematics) {
            this.notificationSender.builder()
                    .fromNotification(this.pluginConfiguration.invalidUsageListMessage)
                    .placeholder("{USAGE}", usage)
                    .send(sender);
        }
    }
}
