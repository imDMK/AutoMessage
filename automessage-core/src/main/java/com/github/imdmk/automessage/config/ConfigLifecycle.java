package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import eu.okaeri.configs.exception.OkaeriException;

final class ConfigLifecycle {

    private final PluginLogger logger;

    ConfigLifecycle(PluginLogger logger) {
        this.logger = logger;
    }

    void initialize(ConfigSection config) {
        config.saveDefaults();
        load(config);
    }

    void load(ConfigSection config) {
        try {
            config.load(true);
        } catch (OkaeriException e) {
            logger.error(e, "Failed to load config %s", config.getClass().getSimpleName());
            throw new ConfigAccessException(e);
        }
    }

    void save(ConfigSection config) {
        try {
            config.save();
        } catch (Exception e) {
            logger.error(e, "Failed to save config %s", config.getClass().getSimpleName());
            throw new ConfigAccessException(e);
        }
    }
}

