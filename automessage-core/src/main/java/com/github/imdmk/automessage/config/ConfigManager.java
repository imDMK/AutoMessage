package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {

    private final Set<ConfigSection> configs = ConcurrentHashMap.newKeySet();
    private final Map<Class<?>, ConfigSection> byType = new ConcurrentHashMap<>();

    private final File dataFolder;

    private final ConfigFactory factory;
    private final ConfigBinder binder;
    private final ConfigLifecycle lifecycle;

    public ConfigManager(PluginLogger logger, File dataFolder) {
        this.dataFolder = dataFolder;

        this.factory = new ConfigFactory();
        this.binder = new ConfigBinder();
        this.lifecycle = new ConfigLifecycle(logger);
    }

    public <C extends ConfigSection> C create(Class<C> type) {
        final C config = factory.create(type);

        initialize(config);
        byType.put(type, config);

        return config;
    }

    /**
     * Initializes a configuration that was constructed by the caller.
     *
     * <p>
     * Language files all share one class and differ only in the file they are bound to, so there
     * is nothing to look them up by type with - the caller holds them instead.
     * </p>
     */
    public <C extends ConfigSection> C create(C config) {
        initialize(config);
        return config;
    }

    private void initialize(ConfigSection config) {
        final File file = new File(dataFolder, config.getFileName());

        binder.bind(config, file);
        lifecycle.initialize(config);

        configs.add(config);
    }

    /** @return the plugin's data folder, so callers can look for files it does not know about */
    public File dataFolder() {
        return dataFolder;
    }

    public void createAll(List<Class<? extends ConfigSection>> types) {
        types.forEach(this::create);
    }

    @SuppressWarnings("unchecked")
    public <C extends ConfigSection> C get(Class<C> type) {
        return (C) byType.get(type);
    }

    public <C extends ConfigSection> C require(Class<C> type) {
        final C config = get(type);
        if (config == null) {
            throw new IllegalStateException("Config not created: " + type.getName());
        }

        return config;
    }

    public void loadAll() {
        configs.forEach(lifecycle::load);
    }

    public void saveAll() {
        configs.forEach(lifecycle::save);
    }

    @Unmodifiable
    public Set<ConfigSection> getConfigs() {
        return Collections.unmodifiableSet(configs);
    }

    public void clearAll() {
        configs.clear();
        byType.clear();
    }

}