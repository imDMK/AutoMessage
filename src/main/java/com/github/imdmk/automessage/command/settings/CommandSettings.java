package com.github.imdmk.automessage.command.settings;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class CommandSettings extends OkaeriConfig {

    @Comment("# Auto message command enabled?")
    public boolean autoMessageEnabled = true;
}
