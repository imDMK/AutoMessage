package com.github.imdmk.automessage.configuration.implementation;

import com.github.imdmk.automessage.configuration.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class PluginConfiguration extends ConfigSection {

    @Comment("# Check for plugin update and send notification after administrator join to server?")
    public boolean checkUpdate = true;

    @Comment("# How often should the plugin check for updates? Recommended value: 1 day")
    public Duration updateInterval = Duration.ofDays(1);

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new SerdesCommons());
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "pluginConfiguration.yml";
    }
}
