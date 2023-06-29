package me.dmk.automessage.command.argument;

import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import me.dmk.automessage.configuration.PluginConfiguration;
import me.dmk.automessage.notification.Notification;
import me.dmk.automessage.notification.NotificationType;
import panda.std.Result;

import java.util.List;
import java.util.stream.Stream;

@ArgumentName("type")
public class NotificationTypeArgument implements OneArgument<NotificationType> {

    private final PluginConfiguration pluginConfiguration;

    public NotificationTypeArgument(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public Result<NotificationType, Notification> parse(LiteInvocation invocation, String argument) {
        return Result.supplyThrowing(IllegalArgumentException.class, () -> NotificationType.valueOf(argument.toUpperCase()))
                .mapErr(e -> this.pluginConfiguration.invalidNotificationTypeMessage);
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return Stream.of(NotificationType.values())
                .map(NotificationType::name)
                .map(Suggestion::of)
                .toList();
    }
}
