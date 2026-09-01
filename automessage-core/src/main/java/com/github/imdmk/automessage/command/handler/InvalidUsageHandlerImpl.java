package com.github.imdmk.automessage.command.handler;

import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerFactory;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;

public final class InvalidUsageHandlerImpl<S> implements InvalidUsageHandler<S> {

    private final MessageService messageService;
    private final ViewerFactory<S> viewers;

    public InvalidUsageHandlerImpl(MessageService messageService, ViewerFactory<S> viewers) {
        this.messageService = messageService;
        this.viewers = viewers;
    }

    @Override
    public void handle(
            Invocation<S> invocation,
            InvalidUsage<S> result,
            ResultHandlerChain<S> chain
    ) {
        final Viewer viewer = viewers.of(invocation.sender());
        final Schematic schematic = result.getSchematic();

        if (schematic.isOnlyFirst()) {
            messageService.create()
                    .viewer(viewer)
                    .notice(notice -> notice.commands.usageInvalid)
                    .placeholder("{USAGE}", schematic.first())
                    .send();
            return;
        }

        messageService.create()
                .viewer(viewer)
                .notice(notice -> notice.commands.usageHeader)
                .send();

        for (final String sch : schematic.all()) {
            messageService.create()
                    .viewer(viewer)
                    .notice(notice -> notice.commands.usageEntry)
                    .placeholder("{USAGE}", sch)
                    .send();
        }
    }
}
