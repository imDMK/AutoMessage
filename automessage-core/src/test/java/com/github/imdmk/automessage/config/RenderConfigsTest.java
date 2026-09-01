package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.language.LanguageRegistry;
import com.github.imdmk.automessage.platform.discord.DiscordWebhookConfig;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.mock;

class RenderConfigsTest {

    @Test
    void render() throws Exception {
        Path dir = Path.of("build/rendered-configs");
        Files.createDirectories(dir);

        ConfigManager manager = new ConfigManager(mock(PluginLogger.class), new File(dir.toString()));

        MessageDispatcherConfig dispatcher = manager.create(MessageDispatcherConfig.class);
        manager.create(ScheduledMessagesConfig.class);
        manager.create(DiscordWebhookConfig.class);

        LanguageRegistry.load(manager, mock(PluginLogger.class), () -> dispatcher.fallbackLanguage);

        manager.saveAll();
    }
}
