package com.github.imdmk.automessage.command.handler;

import com.github.imdmk.automessage.command.argument.UnknownAnnouncementChannel;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;

public final class UnknownAnnouncementChannelHandler<S> implements ResultHandler<S, UnknownAnnouncementChannel> {

    private final MessageService messageService;
    private final ViewerFactory<S> viewers;

    public UnknownAnnouncementChannelHandler(MessageService messageService, ViewerFactory<S> viewers) {
        this.messageService = messageService;
        this.viewers = viewers;
    }

    @Override
    public void handle(
            Invocation<S> invocation,
            UnknownAnnouncementChannel result,
            ResultHandlerChain<S> chain
    ) {
        messageService.create()
                .viewer(viewers.of(invocation.sender()))
                .notice(notice -> notice.commands.channelNotFound)
                .placeholder("{CHANNEL}", result.name())
                .send();
    }
}
