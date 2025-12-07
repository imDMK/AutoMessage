package com.github.imdmk.automessage.platform.litecommands.handler;

import com.github.imdmk.automessage.shared.message.MessageService;
import com.github.imdmk.automessage.shared.validate.Validator;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class MissingPermissionsHandlerImpl implements MissingPermissionsHandler<CommandSender> {

    private final MessageService messageService;

    public MissingPermissionsHandlerImpl(@NotNull MessageService messageService) {
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions permissions, ResultHandlerChain<CommandSender> chain) {
        messageService.create()
                .viewer(invocation.sender())
                .notice(n -> n.liteCommandsMessages.commandPermissionMissing())
                .placeholder("{PERMISSIONS}", String.join(", ", permissions.getPermissions()))
                .send();
    }
}
