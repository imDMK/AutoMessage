package com.github.imdmk.automessage.notification.argument;

import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.notification.settings.NotificationSettings;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import panda.std.Result;

import java.util.List;
import java.util.stream.Stream;

@ArgumentName("position")
public class NotificationTypeArgument implements OneArgument<NotificationType> {

    private final NotificationSettings notificationSettings;

    public NotificationTypeArgument(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    @Override
    public Result<NotificationType, ?> parse(LiteInvocation invocation, String argument) {
        return Result.supplyThrowing(IllegalArgumentException.class, () -> NotificationType.valueOf(argument.toUpperCase()))
                .mapErr(e -> this.notificationSettings.invalidNotificationType);
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return Stream.of(NotificationType.values())
                .map(NotificationType::name)
                .map(String::toUpperCase)
                .map(Suggestion::of)
                .toList();
    }
}
