package com.github.imdmk.automessage.command.next;

import com.github.imdmk.automessage.language.CommandMessages;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.notice.time.DurationFormatter;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.scheduled.dispatcher.ChannelPreview;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherService;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

@Command(name = "automessage next")
@Permission("command.automessage.next")
public final class NextCommand {

    private final MessageDispatcherService dispatcher;
    private final MessageService messageService;

    public NextCommand(MessageDispatcherService dispatcher, MessageService messageService) {
        this.dispatcher = dispatcher;
        this.messageService = messageService;
    }

    @Execute
    void next(@Context Viewer viewer) {
        messageService.send(viewer, notice -> notice.commands.nextHeader);

        for (final ChannelPreview preview : dispatcher.upcoming()) {
            messageService.create()
                    .viewer(viewer)
                    .notice(notice -> lineFor(notice.commands, preview))
                    .placeholder("{CHANNEL}", preview.channel())
                    .placeholder("{MESSAGE}", preview.message() == null ? "" : preview.message())
                    .placeholder("{DELAY}", preview.due() == null ? "" : DurationFormatter.formatCountdown(preview.due()))
                    .send();
        }
    }

    private static Notice lineFor(CommandMessages messages, ChannelPreview preview) {
        return switch (preview.kind()) {
            case NEXT -> messages.nextEntry;
            case UNPREDICTABLE -> messages.nextUnpredictable;
            case DISABLED -> messages.nextDisabled;
            case EMPTY -> messages.nextEmpty;
        };
    }
}
