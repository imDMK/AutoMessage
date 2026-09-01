package com.github.imdmk.automessage.command.view;

import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageSender;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

@Command(name = "automessage view")
@Permission("command.automessage.view")
public final class ViewCommand {

    private final ScheduledMessageSender sender;
    private final MessageService messageService;

    public ViewCommand(ScheduledMessageSender sender, MessageService messageService) {
        this.sender = sender;
        this.messageService = messageService;
    }

    @Execute
    void view(@Context Viewer viewer, @Arg("message") ScheduledMessage message) {
        // A title or a boss bar has nowhere to go on a console, so the preview would be a
        // half-truth at best.
        if (!viewer.isPlayer()) {
            messageService.send(viewer, n -> n.commands.viewPlayerOnly);
            return;
        }

        sender.send(viewer, message);

        messageService.create()
                .viewer(viewer)
                .notice(notice -> notice.commands.messagePreviewed)
                .placeholder("{MESSAGE}", message.name())
                .send();
    }
}
