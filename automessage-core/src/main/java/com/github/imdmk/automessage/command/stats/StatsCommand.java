package com.github.imdmk.automessage.command.stats;

import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.notice.time.DurationFormatter;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.scheduled.dispatcher.DispatchStatistics;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

@Command(name = "automessage stats")
@Permission("command.automessage.stats")
public final class StatsCommand {

    private final DispatchStatistics statistics;
    private final MessageService messageService;

    public StatsCommand(DispatchStatistics statistics, MessageService messageService) {
        this.statistics = statistics;
        this.messageService = messageService;
    }

    @Execute
    void stats(@Context Viewer viewer) {
        final var entries = statistics.snapshot();

        if (entries.isEmpty()) {
            messageService.send(viewer, notice -> notice.commands.statsEmpty);
            return;
        }

        messageService.create()
                .viewer(viewer)
                .notice(notice -> notice.commands.statsHeader)
                .placeholder("{TOTAL}", Long.toString(statistics.total()))
                .send();

        for (final DispatchStatistics.Entry entry : entries) {
            messageService.create()
                    .viewer(viewer)
                    .notice(notice -> notice.commands.statsEntry)
                    .placeholder("{MESSAGE}", entry.message())
                    .placeholder("{COUNT}", Long.toString(entry.count()))
                    .placeholder("{AGO}", DurationFormatter.format(entry.since()))
                    .send();
        }
    }
}
