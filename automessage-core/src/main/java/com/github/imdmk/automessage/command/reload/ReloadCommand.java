package com.github.imdmk.automessage.command.reload;

import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;

@Command(name = "automessage reload")
@Permission("command.automessage.reload")
public final class ReloadCommand {

    private final PluginLogger logger;
    private final ConfigManager configManager;
    private final TaskScheduler taskScheduler;
    private final MessageService messageService;

    public ReloadCommand(
            PluginLogger logger,
            ConfigManager configManager,
            TaskScheduler taskScheduler,
            MessageService messageService
    ) {
        this.logger = logger;
        this.configManager = configManager;
        this.taskScheduler = taskScheduler;
        this.messageService = messageService;
    }

    @Execute
    void reload(@Context CommandSender sender) {
        taskScheduler.runAsync(() -> {
            try {
                configManager.loadAll();
                messageService.send(sender, n -> n.reloadMessages.configReloadedSuccess());
            } catch (Exception e) {
                logger.error(e, "Failed to reload plugin config");
                messageService.send(sender, n -> n.reloadMessages.configReloadFailed());
            }
        });
    }
}
