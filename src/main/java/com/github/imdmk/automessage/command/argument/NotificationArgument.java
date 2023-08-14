package com.github.imdmk.automessage.command.argument;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import com.github.imdmk.automessage.util.StringUtil;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import panda.std.Result;

import java.util.List;
import java.util.stream.IntStream;

public class NotificationArgument implements OneArgument<Notification> {

    private final NotificationConfiguration notificationConfiguration;

    public NotificationArgument(NotificationConfiguration notificationConfiguration) {
        this.notificationConfiguration = notificationConfiguration;
    }

    @Override
    public Result<Notification, ?> parse(LiteInvocation invocation, String argument) {
        if (!StringUtil.isInteger(argument)) {
            return Result.error(this.notificationConfiguration.invalidNumberNotification);
        }

        int position = Integer.parseInt(argument);

        if (position < 0 || position >= this.notificationConfiguration.autoMessages.size()) {
            return Result.error(this.notificationConfiguration.autoMessageNotFoundNotification);
        }

        Notification notification = this.notificationConfiguration.autoMessages.get(position);
        return Result.ok(notification);
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return IntStream.range(0, this.notificationConfiguration.autoMessages.size())
                .mapToObj(String::valueOf)
                .map(Suggestion::of)
                .toList();
    }
}
