package com.github.imdmk.automessage.platform.litecommands.handler;

import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.litecommands.argument.UnknownScheduledMessage;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;

public final class UnknownScheduledMessageHandler implements ResultHandler<CommandSender, UnknownScheduledMessage> {

    private final MessageService messageService;

    public UnknownScheduledMessageHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(
            Invocation<CommandSender> invocation,
            UnknownScheduledMessage result,
            ResultHandlerChain<CommandSender> chain
    ) {
        messageService.create()
                .viewer(invocation.sender())
                .notice(notice -> notice.viewMessages().messageNotFound())
                .placeholder("{MESSAGE}", result.name())
                .send();
    }
}
