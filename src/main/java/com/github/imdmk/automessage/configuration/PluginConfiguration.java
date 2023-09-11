package com.github.imdmk.automessage.configuration;

import com.github.imdmk.automessage.command.configuration.CommandConfiguration;
import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class PluginConfiguration extends OkaeriConfig {

    @Comment("# Check if you are using the latest version when enabling the plugin?")
    public boolean checkForUpdate = true;

    @Comment("# Command configuration")
    public CommandConfiguration commandConfiguration = new CommandConfiguration();

    @Comment("# Notification configuration")
    public NotificationConfiguration notificationConfiguration = new NotificationConfiguration();

}
