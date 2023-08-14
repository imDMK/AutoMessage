package com.github.imdmk.automessage.configuration;

import com.github.imdmk.automessage.notification.configuration.NotificationConfiguration;
import eu.okaeri.configs.OkaeriConfig;

public class PluginConfiguration extends OkaeriConfig {

    public boolean checkForUpdates = true;

    public boolean autoMessageCommandEnabled = true;

    public String autoMessageCommandPermission = "command.automessage";

    public NotificationConfiguration notificationConfiguration = new NotificationConfiguration();

}
