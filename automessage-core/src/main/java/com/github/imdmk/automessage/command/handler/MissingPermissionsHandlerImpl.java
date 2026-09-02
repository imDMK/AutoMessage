package com.github.imdmk.automessage.command.handler;

import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;

public final class MissingPermissionsHandlerImpl<S> implements MissingPermissionsHandler<S> {

    private final MessageService messageService;
    private final ViewerFactory<S> viewers;

    public MissingPermissionsHandlerImpl(MessageService messageService, ViewerFactory<S> viewers) {
        this.messageService = messageService;
        this.viewers = viewers;
    }

    @Override
    public void handle(
            Invocation<S> invocation,
            MissingPermissions permissions,
            ResultHandlerChain<S> chain
    ) {
        messageService.create()
                .viewer(viewers.of(invocation.sender()))
                .notice(n -> n.commands.permissionMissing)
                .placeholder("{PERMISSIONS}", String.join(", ", permissions.getPermissions()))
                .send();
    }
}
