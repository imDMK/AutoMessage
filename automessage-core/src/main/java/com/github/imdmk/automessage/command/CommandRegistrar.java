package com.github.imdmk.automessage.command;

import com.github.imdmk.automessage.AutoMessage;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.command.handler.UnknownAnnouncementChannelHandler;
import com.github.imdmk.automessage.command.argument.UnknownAnnouncementChannel;
import com.github.imdmk.automessage.command.argument.AnnouncementChannelArgument;
import com.github.imdmk.automessage.command.send.SendCommand;
import com.github.imdmk.automessage.command.next.NextCommand;
import com.github.imdmk.automessage.command.stats.StatsCommand;
import com.github.imdmk.automessage.command.argument.ScheduledMessageArgument;
import com.github.imdmk.automessage.command.argument.UnknownScheduledMessage;
import com.github.imdmk.automessage.command.dispatcher.DisableCommand;
import com.github.imdmk.automessage.command.dispatcher.EnableCommand;
import com.github.imdmk.automessage.command.handler.InvalidUsageHandlerImpl;
import com.github.imdmk.automessage.command.handler.MissingPermissionsHandlerImpl;
import com.github.imdmk.automessage.command.handler.NoticeResultHandlerImpl;
import com.github.imdmk.automessage.command.handler.UnknownScheduledMessageHandler;
import com.github.imdmk.automessage.command.reload.ReloadCommand;
import com.github.imdmk.automessage.command.view.ViewCommand;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.context.ContextResult;

public final class CommandRegistrar {

    private CommandRegistrar() {
    }

    public static <S> void configure(
            LiteCommandsBuilder<S, ?, ?> builder,
            AutoMessage automessage,
            ViewerFactory<S> viewers
    ) {
        builder
                .context(Viewer.class, invocation -> ContextResult.ok(() -> viewers.of(invocation.sender())))

                .invalidUsage(new InvalidUsageHandlerImpl<>(automessage.messageService(), viewers))
                .missingPermission(new MissingPermissionsHandlerImpl<>(automessage.messageService(), viewers))
                .result(Notice.class, new NoticeResultHandlerImpl<>(automessage.messageService(), viewers))
                .result(UnknownScheduledMessage.class,
                        new UnknownScheduledMessageHandler<>(automessage.messageService(), viewers))
                .result(UnknownAnnouncementChannel.class,
                        new UnknownAnnouncementChannelHandler<>(automessage.messageService(), viewers))

                .argument(ScheduledMessage.class, new ScheduledMessageArgument<>(automessage.messageRepository()))
                .argument(AnnouncementChannel.class,
                        new AnnouncementChannelArgument<>(automessage.dispatcherConfig()))

                .commands(
                        new DisableCommand(automessage.dispatcherConfig(), automessage.messageService()),
                        new EnableCommand(automessage.dispatcherConfig(), automessage.messageService()),
                        new ReloadCommand(
                                automessage.logger(),
                                automessage.configReloadService(),
                                automessage.scheduler(),
                                automessage.messageService()
                        ),
                        new ViewCommand(automessage.messageSender(), automessage.messageService()),
                        new NextCommand(automessage.dispatcherService(), automessage.messageService()),
                        new StatsCommand(automessage.statistics(), automessage.dispatcherService(),
                                automessage.messageService()),
                        new SendCommand(automessage.dispatcherService(), automessage.messageService())
                );
    }
}
