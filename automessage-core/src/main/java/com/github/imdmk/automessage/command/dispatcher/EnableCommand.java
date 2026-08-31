package com.github.imdmk.automessage.command.dispatcher;

import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;

@Command(name = "automessage enable")
@Permission("command.automessage.enable")
public final class EnableCommand {

    private final MessageDispatcherConfig dispatcherConfig;
    private final MessageService messageService;

    public EnableCommand(
            MessageDispatcherConfig dispatcherConfig,
            MessageService messageService
    ) {
        this.dispatcherConfig = dispatcherConfig;
        this.messageService = messageService;
    }

    @Execute
    void enable(@Context CommandSender sender) {
        if (dispatcherConfig.isEnabled()) {
            messageService.send(sender, n -> n.commands.dispatcherAlreadyEnabled);
            return;
        }

        dispatcherConfig.setEnabled(true);
        dispatcherConfig.save();

        messageService.send(sender, n -> n.commands.dispatcherEnabled);
    }
}
