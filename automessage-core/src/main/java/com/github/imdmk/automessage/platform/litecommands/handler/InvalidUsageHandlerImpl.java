package com.github.imdmk.automessage.platform.litecommands.handler;

import com.github.imdmk.automessage.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

public final class InvalidUsageHandlerImpl implements InvalidUsageHandler<CommandSender> {

    private final MessageService messageService;

    public InvalidUsageHandlerImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(
            Invocation<CommandSender> invocation,
            InvalidUsage<CommandSender> result,
            ResultHandlerChain<CommandSender> chain
    ) {
        final CommandSender sender = invocation.sender();
        final Schematic schematic = result.getSchematic();

        if (schematic.isOnlyFirst()) {
            messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.liteCommandsMessages.commandUsageInvalid())
                    .placeholder("{USAGE}", schematic.first())
                    .send();
            return;
        }

        messageService.create()
                .viewer(sender)
                .notice(notice -> notice.liteCommandsMessages.commandUsageHeader())
                .send();

        for (final String sch : schematic.all()) {
            messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.liteCommandsMessages.commandUsageEntry())
                    .placeholder("{USAGE}", sch)
                    .send();
        }
    }
}
