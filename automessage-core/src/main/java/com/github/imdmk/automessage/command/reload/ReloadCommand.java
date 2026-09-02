package com.github.imdmk.automessage.command.reload;

import com.github.imdmk.automessage.config.ConfigReloadService;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

@Command(name = "automessage reload")
@Permission("command.automessage.reload")
public final class ReloadCommand {

    private final PluginLogger logger;
    private final ConfigReloadService configReloadService;
    private final TaskScheduler taskScheduler;
    private final MessageService messageService;

    public ReloadCommand(
            PluginLogger logger,
            ConfigReloadService configReloadService,
            TaskScheduler taskScheduler,
            MessageService messageService
    ) {
        this.logger = logger;
        this.configReloadService = configReloadService;
        this.taskScheduler = taskScheduler;
        this.messageService = messageService;
    }

    @Execute
    void reload(@Context Viewer viewer) {
        taskScheduler.runAsync(() -> {
            try {
                configReloadService.reload();
                messageService.send(viewer, n -> n.commands.configReloadedSuccess);
            } catch (Exception e) {
                logger.error(e, "Failed to reload plugin config");
                messageService.send(viewer, n -> n.commands.configReloadFailed);
            }
        });
    }
}
