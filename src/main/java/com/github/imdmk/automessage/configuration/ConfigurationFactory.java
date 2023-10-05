package com.github.imdmk.automessage.configuration;

import com.github.imdmk.automessage.configuration.serializer.TitleTimesSerializer;
import com.github.imdmk.automessage.notification.serializer.NotificationSerializer;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;

import java.io.File;

public class ConfigurationFactory {

    private ConfigurationFactory() {
        throw new UnsupportedOperationException("Unsupported operation.");
    }

    public static <T extends OkaeriConfig> T create(Class<T> config, File dataFolder) {
        T configFile = ConfigManager.create(config);

        configFile.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons());
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
}
