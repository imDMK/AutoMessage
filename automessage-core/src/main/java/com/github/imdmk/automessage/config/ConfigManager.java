package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.logger.PluginLogger;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {

    private final Set<ConfigSection> configs = ConcurrentHashMap.newKeySet();

    private final File dataFolder;

    private final ConfigFactory factory;
    private final ConfigBinder binder;
    private final ConfigLifecycle lifecycle;
    private final CapabilityFilter capabilityFilter;

    public ConfigManager(PluginLogger logger, File dataFolder, Capabilities capabilities) {
        this.dataFolder = dataFolder;

        this.factory = new ConfigFactory();
        this.binder = new ConfigBinder();
        this.lifecycle = new ConfigLifecycle(logger);
        this.capabilityFilter = new CapabilityFilter(capabilities);
    }

    public ConfigManager(PluginLogger logger, File dataFolder) {
        this(logger, dataFolder, Capabilities.all());
    }

    public <C extends ConfigSection> C create(Class<C> type) {
        final C config = factory.create(type);

        initialize(config);

        return config;
    }

    public <C extends ConfigSection> C create(C config) {
        initialize(config);
        return config;
    }

    private void initialize(ConfigSection config) {
        final File file = new File(dataFolder, config.getFileName());

        binder.bind(config, file);

        // After binding, which is what builds the declaration, and before the file is written.
        capabilityFilter.apply(config);

        lifecycle.initialize(config);

        configs.add(config);
    }

    public File dataFolder() {
        return dataFolder;
    }

    public void loadAll() {
        configs.forEach(lifecycle::load);
    }

    public void saveAll() {
        configs.forEach(lifecycle::save);
    }

    public void clearAll() {
        configs.clear();
    }

}