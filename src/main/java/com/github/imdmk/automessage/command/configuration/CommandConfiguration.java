package com.github.imdmk.automessage.command.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.util.List;

public class CommandConfiguration extends OkaeriConfig {

    @Comment("# Auto message command enabled?")
    public boolean autoMessageEnabled = true;

    @Comment("# List of auto message command permissions")
    public List<String> autoMessagePermission = List.of("command.automessage");

    @Comment({
            "# List of auto message command aliases",
            "# Example: If you add \"am\" here, you will be able to use the \"/am\" command in game as a shortcut to \"/automessage\"."
    })
    public List<String> autoMessageAliases = List.of(
            "am"
    );
}
