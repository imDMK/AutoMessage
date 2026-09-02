package com.github.imdmk.automessage.command.toggle;

import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.scheduled.audience.optout.AnnouncementOptOut;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

@Command(name = "automessage toggle")
@Permission("command.automessage.toggle")
public final class ToggleCommand {

    private final AnnouncementOptOut optOut;
    private final TaskScheduler scheduler;
    private final MessageService messageService;

    public ToggleCommand(AnnouncementOptOut optOut, TaskScheduler scheduler, MessageService messageService) {
        this.optOut = optOut;
        this.scheduler = scheduler;
        this.messageService = messageService;
    }

    @Execute
    void toggle(@Context Viewer viewer) {
        // The console receives announcements from nowhere, and a preference stored against its
        // fixed id would be a preference nobody set.
        if (!viewer.isPlayer()) {
            messageService.send(viewer, notice -> notice.commands.togglePlayerOnly);
            return;
        }

        final boolean muted = optOut.toggle(viewer.uniqueId());

        // Answered from memory the moment it is toggled; the file only has to be there after a
        // restart, so writing it waits for a thread that is allowed to touch a disk.
        scheduler.runAsync(optOut::save);

        messageService.send(viewer, notice -> muted
                ? notice.commands.toggleOff
                : notice.commands.toggleOn);
    }
}
