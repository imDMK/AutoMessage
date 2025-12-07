package com.github.imdmk.automessage.command.dispatcher;

import com.github.imdmk.automessage.scheduled.dispatcher.MessagesDispatcherConfig;
import com.github.imdmk.automessage.shared.message.MessageService;
import com.github.imdmk.automessage.shared.validate.Validator;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Command(name = "automessage disable")
@Permission("command.automessage.disable")
public final class DisableCommand {

    private final MessagesDispatcherConfig dispatcherConfig;
    private final MessageService messageService;

    public DisableCommand(
            @NotNull MessagesDispatcherConfig dispatcherConfig,
            @NotNull MessageService messageService
    ) {
        this.dispatcherConfig = Validator.notNull(dispatcherConfig, "dispatcherConfig");
        this.messageService = Validator.notNull(messageService, "messageService");
    }

    @Execute
    void disable(@Context CommandSender sender) {
        if (!dispatcherConfig.isEnabled()) {
            messageService.send(sender, n -> n.dispatcherMessages.dispatcherAlreadyDisabled());
            return;
        }

        dispatcherConfig.setEnabled(false);
        dispatcherConfig.save();

        messageService.send(sender, n -> n.dispatcherMessages.dispatcherDisabled());
    }
}