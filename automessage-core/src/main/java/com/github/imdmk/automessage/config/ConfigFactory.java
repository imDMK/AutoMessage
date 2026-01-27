package com.github.imdmk.automessage.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.exception.OkaeriException;

final class ConfigFactory {

    <T extends ConfigSection> T create(Class<T> type) {
        try {
            return ConfigManager.create(type);
        } catch (OkaeriException e) {
            throw new IllegalStateException("Failed to create config: " + type.getName(), e);
        }
    }
}

