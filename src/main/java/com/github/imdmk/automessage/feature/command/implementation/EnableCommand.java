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

@Command(name = "automessage enable")
@Permission("command.automessage.enable")
public class EnableCommand {

    private final MessageService messageService;
    private final AutoMessageDispatcher dispatcher;

    public EnableCommand(@NotNull MessageService messageService, @NotNull AutoMessageDispatcher dispatcher) {
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher cannot be null");
    }

    @Execute
    void enable(@Context CommandSender sender) {
        if (this.dispatcher.isEnabled()) {
            this.messageService.send(sender, notice -> notice.autoMessageAlreadyEnabled);
            return;
        }

        this.dispatcher.setEnabled(true);
        this.dispatcher.schedule(); // Reset task

        this.messageService.send(sender, notice -> notice.autoMessageEnable);
    }
}
