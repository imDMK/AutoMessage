package com.github.imdmk.automessage.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ConfigReloadService {

    private final ConfigManager configManager;
    private final List<ConfigReloadListener> listeners = new CopyOnWriteArrayList<>();

    public ConfigReloadService(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void register(ConfigReloadListener listener) {
        listeners.add(listener);
    }

    public void reload() {
        configManager.loadAll();
        listeners.forEach(ConfigReloadListener::onConfigReload);
    }
}
