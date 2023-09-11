package com.github.imdmk.automessage.command.editor;

import com.github.imdmk.automessage.command.configuration.CommandConfiguration;
import dev.rollczi.litecommands.factory.CommandEditor;

import java.util.List;

public class AutoMessageCommandEditor implements CommandEditor {

    private final CommandConfiguration commandConfiguration;

    public AutoMessageCommandEditor(CommandConfiguration commandConfiguration) {
        this.commandConfiguration = commandConfiguration;
    }

    @Override
    public State edit(State state) {
        List<String> autoMessageCommandPermission = this.commandConfiguration.autoMessagePermission;
        List<String> autoMessageCommandAliases = this.commandConfiguration.autoMessageAliases;

        state.permission(autoMessageCommandPermission);
        state.aliases(autoMessageCommandAliases);

        return state;
    }
}
