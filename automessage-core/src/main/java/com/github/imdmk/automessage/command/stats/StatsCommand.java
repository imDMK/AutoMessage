package com.github.imdmk.automessage.command.stats;

import com.github.imdmk.automessage.language.CommandMessages;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.notice.time.DurationFormatter;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.scheduled.dispatcher.ChannelPreview;
import com.github.imdmk.automessage.scheduled.dispatcher.DispatchStatistics;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherService;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

import java.util.Optional;

@Command(name = "automessage stats")
@Permission("command.automessage.stats")
public final class StatsCommand {

    private final DispatchStatistics statistics;
    private final MessageDispatcherService dispatcher;
    private final MessageService messageService;

    public StatsCommand(
            DispatchStatistics statistics,
            MessageDispatcherService dispatcher,
            MessageService messageService
    ) {
        this.statistics = statistics;
        this.dispatcher = dispatcher;
        this.messageService = messageService;
    }

    @Execute
    void stats(@Context Viewer viewer) {
        final long total = statistics.total();

        messageService.create()
                .viewer(viewer)
                .notice(notice -> total == 0 ? notice.commands.statsEmpty : notice.commands.statsHeader)
                .placeholder("{TOTAL}", Long.toString(total))
                .send();

        // Per channel first, because that is the line with a future in it - what has gone out and
        // when the next one is due are the same question asked backwards and forwards.
        for (final ChannelPreview preview : dispatcher.upcoming()) {
            sendChannelLine(viewer, preview);
        }

        for (final DispatchStatistics.Entry entry : statistics.snapshot()) {
            messageService.create()
                    .viewer(viewer)
                    .notice(notice -> notice.commands.statsEntry)
                    .placeholder("{MESSAGE}", entry.name())
                    .placeholder("{COUNT}", Long.toString(entry.count()))
                    .placeholder("{AGO}", DurationFormatter.formatReadable(entry.since()))
                    .send();
        }
    }

    private void sendChannelLine(Viewer viewer, ChannelPreview preview) {
        final Optional<DispatchStatistics.Entry> counted = statistics.channel(preview.channel());
        final long count = counted.map(DispatchStatistics.Entry::count).orElse(0L);

        messageService.create()
                .viewer(viewer)
                .notice(notice -> channelLine(notice.commands, preview, count))
                .placeholder("{CHANNEL}", preview.channel())
                .placeholder("{COUNT}", Long.toString(count))
                .placeholder("{AGO}", counted
                        .map(entry -> DurationFormatter.formatReadable(entry.since()))
                        .orElse(""))
                .placeholder("{DELAY}", preview.due() == null
                        ? ""
                        : DurationFormatter.formatReadable(preview.due()))
                .send();
    }

    // A channel with no countdown is switched off or has nothing assigned; /automessage next is
    // the command that explains which, so this one only says it is not counting down.
    private static Notice channelLine(CommandMessages messages, ChannelPreview preview, long count) {
        if (preview.due() == null) {
            return messages.statsChannelIdle;
        }

        return count == 0 ? messages.statsChannelPending : messages.statsChannel;
    }
}
