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

@Command(name = "automessage enable")
@Permission("command.automessage.enable")
public final class EnableCommand {

    private final MessageDispatcherConfig dispatcherConfig;
    private final MessageService messageService;

    public EnableCommand(
            @NotNull MessageDispatcherConfig dispatcherConfig,
            @NotNull MessageService messageService
    ) {
        this.dispatcherConfig = Validator.notNull(dispatcherConfig, "dispatcherConfig");
        this.messageService = Validator.notNull(messageService, "messageService");
    }

    @Execute
    void enable(@Context CommandSender sender) {
        dispatcherConfig.setEnabled(true);
        messageService.send(sender, n -> n.dispatcherMessages.dispatcherEnabled());
    }
}
