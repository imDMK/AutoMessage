package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.shared.validate.Validator;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {

    private final Set<ConfigSection> configs = ConcurrentHashMap.newKeySet();
    private final Map<Class<?>, ConfigSection> byType = new ConcurrentHashMap<>();

    private final PluginLogger logger;
    private final File dataFolder;

    public ConfigManager(@NotNull PluginLogger logger, @NotNull File dataFolder) {
        this.logger = Validator.notNull(logger, "logger");
        this.dataFolder = Validator.notNull(dataFolder, "dataFolder");
    }

    public <T extends ConfigSection> @NotNull T create(@NotNull Class<T> configClass) {
        final T config = eu.okaeri.configs.ConfigManager.create(configClass);

        final String fileName = config.getFileName();
        if (fileName.isBlank()) {
            throw new IllegalStateException(
                    "Missing config file name for " + configClass.getName()
                            + " – override getFileName() to return a non-empty path, e.g. 'config.yml'."
            );
        }

        final OkaeriSerdesPack serdes = Validator.notNull(config.getSerdesPack(), "config serdes pack");

        final File file = new File(dataFolder, fileName);
        final YamlSnakeYamlConfigurer configurer = createYamlSnakeYamlConfigurer();

        config.withConfigurer(configurer, serdes);
        config.withSerdesPack(new SerdesCommons());
        config.withBindFile(file);
        config.withRemoveOrphans(true);
        config.saveDefaults();
        config.load(true);

        configs.add(config);
        byType.put(configClass, config);
        return config;
    }

    public void createAll(@NotNull List<Class<? extends ConfigSection>> configClasses) {
        configClasses.forEach(this::create);
    }

    @SuppressWarnings("unchecked")
    public <T extends ConfigSection> T get(@NotNull Class<T> type) {
        return (T) byType.get(type);
    }

    public <T extends ConfigSection> @NotNull T require(@NotNull Class<T> type) {
        final T config = get(type);
        if (config == null) {
            throw new IllegalStateException("Config not created: " + type.getName());
        }
        return config;
    }

    private @NotNull YamlSnakeYamlConfigurer createYamlSnakeYamlConfigurer() {
        final LoaderOptions loader = new LoaderOptions();
        loader.setAllowRecursiveKeys(false);
        loader.setMaxAliasesForCollections(50);

        final Constructor constructor = new Constructor(loader);

        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setSplitLines(false);

        final Representer representer = new ConfigRepresenter(options);
        final Resolver resolver = new Resolver();

        final Yaml yaml = new Yaml(constructor, representer, options, loader, resolver);
        return new YamlSnakeYamlConfigurer(yaml);
    }

    public void loadAll() {
        configs.forEach(this::load);
    }

    private void load(@NotNull ConfigSection config) {
        try {
            config.load(true);
        } catch (OkaeriException e) {
            logger.error(e, "Failed to load config: %s", config.getClass().getSimpleName());
            throw new ConfigLoadException(e);
        }
    }

    public void saveAll() {
        configs.forEach(this::save);
    }

    private void save(@NotNull ConfigSection config) {
        try {
            config.save();
        } catch (OkaeriException e) {
            logger.error(e, "Failed to save config: %s", config.getClass().getSimpleName());
            throw new ConfigLoadException(e);
        }
    }

    public @NotNull @Unmodifiable Set<ConfigSection> getConfigs() {
        return Collections.unmodifiableSet(configs);
    }

    public void clearAll() {
        configs.clear();
        byType.clear();
    }
}
