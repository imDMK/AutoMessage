package com.github.imdmk.automessage.config;

import eu.okaeri.configs.serdes.commons.SerdesCommons;

import java.io.File;

final class ConfigBinder {

    void bind(ConfigSection config, File file) {
        config.withConfigurer(YamlConfigurerFactory.create())
                .withSerdesPack(config.getSerdesPack())
                .withSerdesPack(new SerdesCommons())
                .withBindFile(file)
                .withRemoveOrphans(true);
    }
}

