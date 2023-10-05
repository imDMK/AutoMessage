package com.github.imdmk.automessage.command.argument;

import com.github.imdmk.automessage.notification.settings.NotificationSettings;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import panda.std.Result;

import java.util.List;

@ArgumentName("progress")
public class BossBarProgressArgument implements OneArgument<Float> {

    private final NotificationSettings notificationSettings;

    public BossBarProgressArgument(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    @Override
    public Result<Float, ?> parse(LiteInvocation invocation, String argument) {
        return Result.supplyThrowing(NumberFormatException.class, () -> Float.parseFloat(argument))
                .filter(this::isValid, e -> new NumberFormatException("The float value must be greater than 0 and less than 1.0"))
                .mapErr(exception -> this.notificationSettings.invalidFloatNotification);
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return Suggestion.of("0.1", "0.5", "1.0");
    }

    private boolean isValid(float value) {
        return value > 0.0F && value <= 1.0F;
    }
}
