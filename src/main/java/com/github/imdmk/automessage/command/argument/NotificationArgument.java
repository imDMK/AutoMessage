package com.github.imdmk.automessage.command.argument;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import com.github.imdmk.automessage.util.StringUtil;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import panda.std.Result;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@ArgumentName("notificationPosition")
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

        Optional<Notification> notificationOptional = this.notificationConfiguration.autoMessages.stream()
                .skip(position)
                .findFirst();

        return notificationOptional.map(Result::ok)
                .orElseGet(() -> Result.error(this.notificationConfiguration.autoMessageNotFoundNotification));
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        int autoMessagesSize = this.notificationConfiguration.autoMessages.size();

        return IntStream.range(0, autoMessagesSize)
                .mapToObj(String::valueOf)
                .map(Suggestion::of)
                .toList();
    }
}
