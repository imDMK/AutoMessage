package com.github.imdmk.automessage.command.implementation.editor;

import com.github.imdmk.automessage.command.settings.CommandSettings;
import dev.rollczi.litecommands.factory.CommandEditor;

public class AutoMessageCommandEditor implements CommandEditor {

    private final CommandSettings commandSettings;

    public AutoMessageCommandEditor(CommandSettings commandSettings) {
        this.commandSettings = commandSettings;
    }

    @Override
    public State edit(State state) {
        state.permission(this.commandSettings.autoMessagePermissions);

        return state;
    }
}
