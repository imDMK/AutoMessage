package com.github.imdmk.automessage.feature.command.implementation;

import com.github.imdmk.automessage.feature.message.MessageService;
import com.github.imdmk.automessage.feature.message.auto.dispatcher.AutoMessageDispatcher;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Command(name = "automessage disable")
@Permission("command.automessage.disable")
public class DisableCommand {

    private final MessageService messageService;
    private final AutoMessageDispatcher dispatcher;

    public DisableCommand(@NotNull MessageService messageService, @NotNull AutoMessageDispatcher dispatcher) {
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher cannot be null");
    }

    @Execute
    void enable(@Context CommandSender sender) {
        if (!this.dispatcher.isEnabled()) {
            this.messageService.send(sender, notice -> notice.autoMessageAlreadyDisabled);
            return;
        }

        this.dispatcher.setEnabled(false);
        this.dispatcher.cancel(); // Cancel task

        this.messageService.send(sender, notice -> notice.autoMessageDisable);
    }
}
