package me.dmk.automessage.command.argument;

import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import me.dmk.automessage.configuration.PluginConfiguration;
import me.dmk.automessage.notification.Notification;
import me.dmk.automessage.notification.NotificationSender;
import me.dmk.automessage.util.StringUtil;
import panda.std.Result;

import java.util.List;
import java.util.stream.IntStream;

@ArgumentName("position")
public class NotificationArgument implements OneArgument<Notification> {

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSender notificationSender;

    public NotificationArgument(PluginConfiguration pluginConfiguration, NotificationSender notificationSender) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSender = notificationSender;
    }

    @Override
    public Result<Notification, ?> parse(LiteInvocation invocation, String argument) {
         if (!StringUtil.isInteger(argument)) {
             return Result.error(this.pluginConfiguration.invalidNumberMessage);
         }

         int position = Integer.parseInt(argument);

         if (position < 0 || position >= this.pluginConfiguration.autoMessages.size()) {
             Notification notification = this.notificationSender.builder()
                     .fromNotification(this.pluginConfiguration.notificationNotFoundMessage)
                     .placeholder("{POSITION}", position)
                     .build();

             return Result.error(notification);
         }

         Notification notification = this.pluginConfiguration.autoMessages.get(position);
         return Result.ok(notification);
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return IntStream.range(0, this.pluginConfiguration.autoMessages.size())
                .mapToObj(String::valueOf)
                .map(Suggestion::of)
                .toList();
    }
}
