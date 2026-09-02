package com.github.imdmk.automessage.command.dispatcher;

import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

@Command(name = "automessage disable")
@Permission("command.automessage.disable")
public final class DisableCommand {

    private final MessageDispatcherConfig dispatcherConfig;
    private final MessageService messageService;

    public DisableCommand(MessageDispatcherConfig dispatcherConfig, MessageService messageService) {
        this.dispatcherConfig = dispatcherConfig;
        this.messageService = messageService;
    }

    @Execute
    void disable(@Context Viewer viewer) {
        if (!dispatcherConfig.isEnabled()) {
            messageService.send(viewer, n -> n.commands.dispatcherAlreadyDisabled);
            return;
        }

        dispatcherConfig.setEnabled(false);
        dispatcherConfig.save();

        messageService.send(viewer, n -> n.commands.dispatcherDisabled);
    }
}
