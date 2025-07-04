package com.github.imdmk.automessage.feature.command.builder.configuration;

import dev.rollczi.litecommands.command.builder.CommandBuilder;
import dev.rollczi.litecommands.editor.Editor;
import dev.rollczi.litecommands.meta.Meta;
import dev.rollczi.litecommands.permission.PermissionSet;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Logger;

public class CommandEditor implements Editor<CommandSender> {

    private final Logger logger;
    private final CommandConfig configuration;

    public CommandEditor(@NotNull Logger logger, @NotNull CommandConfig configuration) {
        this.logger = logger;
        this.configuration = configuration;
    }

    @Override
    public CommandBuilder<CommandSender> edit(CommandBuilder<CommandSender> context) {
        if (!this.configuration.enabled) {
            return context;
        }

        return this.configuration.getCommand(context.name())
                .map(command -> {
                    CommandBuilder<CommandSender> updated = this.updateCommand(context, command);
                    return this.updateSubCommand(updated, command.subCommands());
                })
                .map(command -> {
                    this.logger.info("Edited command " + command.name() + " via configuration.");
                    return command;
                })
                .orElse(context);
    }

    private @NotNull CommandBuilder<CommandSender> updateCommand(@NotNull CommandBuilder<CommandSender> context, @NotNull Command command) {
        return context
                .name(command.name())
                .aliases(command.aliases())
                .applyMeta(meta -> meta.list(Meta.PERMISSIONS, permissions -> permissions.add(new PermissionSet(command.permissions()))))
                .enabled(command.isEnabled());
    }

    private @NotNull CommandBuilder<CommandSender> updateSubCommand(@NotNull CommandBuilder<CommandSender> context, @NotNull Map<String, SubCommand> subCommands) {
        for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
            String id = entry.getKey();
            SubCommand sub = entry.getValue();

            context = context.editChild(id, child -> child
                    .name(sub.name())
                    .aliases(sub.aliases())
                    .applyMeta(meta -> meta.list(Meta.PERMISSIONS, permissions -> permissions.addAll(new PermissionSet(sub.permissions()))))
                    .enabled(sub.isEnabled()));
        }

        return context;
    }
}
