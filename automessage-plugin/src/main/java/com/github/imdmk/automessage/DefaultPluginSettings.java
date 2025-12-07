package com.github.imdmk.automessage;

import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import com.github.imdmk.automessage.shared.message.MessageConfig;

import java.util.List;

public final class DefaultPluginSettings implements PluginSettings {

    @Override
    public List<Class<? extends ConfigSection>> configs() {
        return List.of(
                MessageConfig.class,
                ScheduledMessagesConfig.class,
                MessageDispatcherConfig.class
        );
    }
}
