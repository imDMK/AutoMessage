package com.github.imdmk.automessage.command.argument;

import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import panda.std.Result;

import java.util.List;

public class BossBarProgressArgument implements OneArgument<Float> {

    private final NotificationConfiguration notificationConfiguration;

    public BossBarProgressArgument(NotificationConfiguration notificationConfiguration) {
        this.notificationConfiguration = notificationConfiguration;
    }

    @Override
    public Result<Float, ?> parse(LiteInvocation invocation, String argument) {
        return Result.supplyThrowing(NumberFormatException.class, () -> Float.parseFloat(argument))
                .mapErr(exception -> this.notificationConfiguration.invalidNumberNotification);
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return Suggestion.of("0.1", "0.5", "1.0");
    }
}
