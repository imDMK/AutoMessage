package me.dmk.automessage.command.editor;

import dev.rollczi.litecommands.factory.CommandEditor;
import me.dmk.automessage.configuration.PluginConfiguration;

import java.util.List;

public class CommandPermissionEditor implements CommandEditor {

    private final PluginConfiguration pluginConfiguration;

    public CommandPermissionEditor(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public State edit(State state) {
        return state.permission(List.of(
                this.pluginConfiguration.autoMessageCommandPermission
        ));
    }
}
