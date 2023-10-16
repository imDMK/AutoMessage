package com.github.imdmk.automessage.notification.argument;

import com.github.imdmk.automessage.configuration.implementation.PluginConfiguration;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.configuration.NotificationSettings;
import com.github.imdmk.automessage.util.CollectionUtil;
import com.github.imdmk.automessage.util.StringUtil;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import panda.std.Result;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@ArgumentName("position")
public class NotificationArgument implements OneArgument<Notification> {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSettings notificationSettings;

    public NotificationArgument(PluginConfiguration pluginConfiguration, NotificationSettings notificationSettings) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSettings = notificationSettings;
    }

    @Override
    public Result<Notification, ?> parse(LiteInvocation invocation, String argument) {
        if (!StringUtil.isInteger(argument)) {
            return Result.error(this.notificationSettings.invalidNumber);
        }

        int position = Integer.parseInt(argument);

        List<Notification> autoMessages = this.pluginConfiguration.autoMessages;

        Optional<Notification> selectedNotification = CollectionUtil.select(autoMessages, position);

        return selectedNotification
                .map(Result::ok)
                .orElseGet(() -> Result.error(this.notificationSettings.autoMessageNotFound));
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        int autoMessagesSize = this.pluginConfiguration.autoMessages.size();

        return IntStream.range(0, autoMessagesSize)
                .mapToObj(String::valueOf)
                .map(Suggestion::of)
                .toList();
    }
}
