package com.github.imdmk.automessage.command.send;

import com.github.imdmk.automessage.language.CommandMessages;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.notice.time.DurationFormatter;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.dispatcher.ForcedSend;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

// Deliberately its own command rather than an argument of /automessage next. That one answers a
// question; this one broadcasts to everybody online, and the two should not be separated by a
// single word somebody could tab-complete by accident.
@Command(name = "automessage send")
@Permission("command.automessage.send")
public final class SendCommand {

    private final MessageDispatcherService dispatcher;
    private final MessageService messageService;

    public SendCommand(MessageDispatcherService dispatcher, MessageService messageService) {
        this.dispatcher = dispatcher;
        this.messageService = messageService;
    }

    @Execute
    void send(@Context Viewer viewer, @Arg("channel") AnnouncementChannel channel) {
        final ForcedSend result = dispatcher.forceNext(channel);

        messageService.create()
                .viewer(viewer)
                .notice(notice -> lineFor(notice.commands, result))
                .placeholder("{CHANNEL}", channel.name())
                .placeholder("{MESSAGE}", result.message().map(ScheduledMessage::name).orElse(""))
                .placeholder("{DELAY}", result.nextIn() == null ? "" : DurationFormatter.formatReadable(result.nextIn()))
                .send();
    }

    private static Notice lineFor(CommandMessages messages, ForcedSend result) {
        return switch (result.kind()) {
            case SENT -> messages.sendDone;
            case NOBODY_ONLINE -> messages.sendNobodyOnline;
            case NO_MESSAGES -> messages.sendNoMessages;
            case DISABLED -> messages.sendDisabled;
        };
    }
}
