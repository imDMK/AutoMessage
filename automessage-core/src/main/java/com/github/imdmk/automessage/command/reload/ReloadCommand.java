package com.github.imdmk.automessage.command.reload;

import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.shared.message.MessageService;
import com.github.imdmk.automessage.shared.validate.Validator;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@Command(name = "automessage reload")
@Permission("command.automessage.reload")
public final class ReloadCommand {

    private final PluginLogger logger;
    private final ConfigManager configManager;
    private final MessageService messageService;

    public ReloadCommand(
            @NotNull PluginLogger logger,
            @NotNull ConfigManager configManager,
            @NotNull MessageService messageService
    ) {
        this.logger = Validator.notNull(logger, "logger");
        this.configManager = Validator.notNull(configManager, "configManager");
        this.messageService = Validator.notNull(messageService, "messageService");
    }

    @Execute
    void reload(@Context CommandSender sender) {
        CompletableFuture.runAsync(configManager::loadAll)
                .thenAccept(v -> messageService.send(sender, n -> n.reloadMessages.configReloadedSuccess()))
                .exceptionally(e -> {
                    logger.error(e, "Failed to reload plugin config");
                    messageService.send(sender, n -> n.reloadMessages.configReloadFailed());
                    return null;
                });
    }
}
