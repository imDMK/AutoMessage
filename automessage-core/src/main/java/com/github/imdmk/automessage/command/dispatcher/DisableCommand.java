package com.github.imdmk.automessage.command.dispatcher;

import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
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

    private final MessageDispatcherConfig dispatcherConfig;
    private final MessageService messageService;

    public DisableCommand(
            @NotNull MessageDispatcherConfig dispatcherConfig,
            @NotNull MessageService messageService
    ) {
        this.dispatcherConfig = Validator.notNull(dispatcherConfig, "dispatcherConfig");
        this.messageService = Validator.notNull(messageService, "messageService");
    }

    @Execute
    void disable(@Context CommandSender sender) {
        dispatcherConfig.setEnabled(false);
        messageService.send(sender, n -> n.dispatcherMessages.dispatcherDisabled());
    }
}