package com.github.imdmk.automessage.command.settings;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.util.List;

public class CommandSettings extends OkaeriConfig {

    @Comment("# Auto message command enabled?")
    public boolean autoMessageEnabled = true;

    @Comment("# List of auto message command permissions")
    public List<String> autoMessagePermissions = List.of("command.automessage");
}
