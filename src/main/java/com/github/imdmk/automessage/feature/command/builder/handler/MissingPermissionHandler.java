package com.github.imdmk.automessage.feature.command.builder.handler;

import com.github.imdmk.automessage.feature.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MissingPermissionHandler implements MissingPermissionsHandler<CommandSender> {

    private final MessageService messageService;

    public MissingPermissionHandler(@NotNull MessageService messageService) {
        this.messageService = Objects.requireNonNull(messageService, "message service cannot be null");
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions permissions, ResultHandlerChain<CommandSender> chain) {
        this.messageService.create()
                .viewer(invocation.sender())
                .notice(notice -> notice.missingPermissions)
                .placeholder("{PERMISSIONS}", String.join(", ", permissions.getPermissions()))
                .send();
    }
}
