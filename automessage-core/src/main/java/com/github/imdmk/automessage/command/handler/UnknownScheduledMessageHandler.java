package com.github.imdmk.automessage.command.handler;

import com.github.imdmk.automessage.command.argument.UnknownScheduledMessage;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;

public final class UnknownScheduledMessageHandler<S> implements ResultHandler<S, UnknownScheduledMessage> {

    private final MessageService messageService;
    private final ViewerFactory<S> viewers;

    public UnknownScheduledMessageHandler(MessageService messageService, ViewerFactory<S> viewers) {
        this.messageService = messageService;
        this.viewers = viewers;
    }

    @Override
    public void handle(
            Invocation<S> invocation,
            UnknownScheduledMessage result,
            ResultHandlerChain<S> chain
    ) {
        messageService.create()
                .viewer(viewers.of(invocation.sender()))
                .notice(notice -> notice.commands.messageNotFound)
                .placeholder("{MESSAGE}", result.name())
                .send();
    }
}
