package com.github.imdmk.automessage.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Reloads every registered configuration file and lets the rest of the plugin re-apply
 * the freshly loaded values.
 *
 * <p>
 * Reading a configuration field on every use is enough for most settings, but not for values
 * consumed once — such as the dispatcher interval, which Bukkit captures when the task is
 * scheduled. Listeners registered here close that gap, so {@code /automessage reload} applies
 * the whole configuration instead of only part of it.
 * </p>
 */
public final class ConfigReloadService {

    private final ConfigManager configManager;
    private final List<ConfigReloadListener> listeners = new CopyOnWriteArrayList<>();

    public ConfigReloadService(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void register(ConfigReloadListener listener) {
        listeners.add(listener);
    }

    /**
     * Loads every configuration file from disk and notifies all listeners.
     *
     * @throws ConfigAccessException when any configuration file could not be loaded;
     *                               listeners are not notified in that case
     */
    public void reload() {
        configManager.loadAll();
        listeners.forEach(ConfigReloadListener::onConfigReload);
    }
}
