package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.capability.Capabilities;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

public abstract class ConfigSection extends OkaeriConfig {

    public void applyCapabilities(Capabilities capabilities) {
    }

    public abstract OkaeriSerdesPack getSerdesPack();

    public abstract String getFileName();
}
