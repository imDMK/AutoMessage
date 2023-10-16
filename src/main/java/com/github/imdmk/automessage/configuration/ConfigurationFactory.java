package com.github.imdmk.automessage.configuration;

import com.github.imdmk.automessage.configuration.representer.CustomRepresenter;
import com.github.imdmk.automessage.configuration.serializer.TitleTimesSerializer;
import com.github.imdmk.automessage.notification.configuration.NotificationSerializer;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.File;

public class ConfigurationFactory {

    private ConfigurationFactory() {
        throw new UnsupportedOperationException("Unsupported operation.");
    }

    public static <T extends OkaeriConfig> T create(Class<T> config, File dataFolder) {
        T configFile = ConfigManager.create(config);

        YamlSnakeYamlConfigurer yamlSnakeYamlConfigurer = createYamlSnakeYamlConfigurer();

        configFile.withConfigurer(yamlSnakeYamlConfigurer, new SerdesCommons());
        configFile.withSerdesPack(registry -> {
            registry.register(new TitleTimesSerializer());
            registry.register(new NotificationSerializer());
        });

        configFile.withBindFile(dataFolder);
        configFile.withRemoveOrphans(true);
        configFile.saveDefaults();
        configFile.load(true);

        return configFile;
    }

    private static YamlSnakeYamlConfigurer createYamlSnakeYamlConfigurer() {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(loaderOptions);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
        dumperOptions.setIndent(2);
        dumperOptions.setSplitLines(false);

        Representer representer = new CustomRepresenter(dumperOptions);
        Resolver resolver = new Resolver();

        Yaml yaml = new Yaml(constructor, representer, dumperOptions, loaderOptions, resolver);
        return new YamlSnakeYamlConfigurer(yaml);
    }
}
