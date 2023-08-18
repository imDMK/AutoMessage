package com.github.imdmk.automessage.configuration;

import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class PluginConfiguration extends OkaeriConfig {

    @Comment("# Check if you are using the latest version when enabling the plugin?")
    public boolean checkForUpdate = true;

    @Comment("# Auto message command enabled?")
    public boolean autoMessageCommandEnabled = true;

    @Comment("# Auto message command permission")
    public String autoMessageCommandPermission = "command.automessage";

    @Comment("# Notification configuration")
    public NotificationConfiguration notificationConfiguration = new NotificationConfiguration();

}
