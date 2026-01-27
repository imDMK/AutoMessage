package com.github.imdmk.automessage.platform.litecommands.handler;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;

public final class NoticeResultHandlerImpl implements ResultHandler<CommandSender, Notice> {

    private final MessageService messageService;

    public NoticeResultHandlerImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(
            Invocation<CommandSender> invocation,
            Notice notice,
            ResultHandlerChain<CommandSender> chain
    ) {
        messageService.send(invocation.sender(), n -> notice);
    }
}
