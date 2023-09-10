package com.github.imdmk.automessage.command.editor;

import com.github.imdmk.automessage.configuration.PluginConfiguration;
import dev.rollczi.litecommands.factory.CommandEditor;

import java.util.List;

public class AutoMessageCommandEditor implements CommandEditor {

    private final PluginConfiguration pluginConfiguration;

    public AutoMessageCommandEditor(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public State edit(State state) {
        state.permission(List.of(this.pluginConfiguration.autoMessageCommandPermission));
        return state;
    }
}
