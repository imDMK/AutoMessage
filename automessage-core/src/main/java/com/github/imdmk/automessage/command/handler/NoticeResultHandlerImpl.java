package com.github.imdmk.automessage.command.handler;

import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;

public final class NoticeResultHandlerImpl<S> implements ResultHandler<S, Notice> {

    private final MessageService messageService;
    private final ViewerFactory<S> viewers;

    public NoticeResultHandlerImpl(MessageService messageService, ViewerFactory<S> viewers) {
        this.messageService = messageService;
        this.viewers = viewers;
    }

    @Override
    public void handle(
            Invocation<S> invocation,
            Notice notice,
            ResultHandlerChain<S> chain
    ) {
        messageService.send(viewers.of(invocation.sender()), n -> notice);
    }
}
