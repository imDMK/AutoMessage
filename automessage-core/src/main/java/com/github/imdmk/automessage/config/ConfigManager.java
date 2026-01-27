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
        final File file = new File(dataFolder, config.getFileName());

        binder.bind(config, file);
        lifecycle.initialize(config);

        register(type, config);
        return config;
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

    private void register(Class<?> type, ConfigSection config) {
        configs.add(config);
        byType.put(type, config);
    }
}