package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.capability.Capability;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CapabilityFilterTest {

    @TempDir
    Path dataFolder;

    public static final class PlatformDependentConfig extends ConfigSection {

        public boolean enabled = true;

        @RequiresCapability(Capability.WORLD_RULE)
        public String defaultWorld = "world";

        @Override
        public OkaeriSerdesPack getSerdesPack() {
            return registry -> {
            };
        }

        @Override
        public String getFileName() {
            return "platform-dependent.yml";
        }
    }

    private String write(Capabilities capabilities) throws IOException {
        final ConfigManager manager =
                new ConfigManager(mock(PluginLogger.class), dataFolder.toFile(), capabilities);

        manager.create(PlatformDependentConfig.class);
        manager.saveAll();

        return Files.readString(new File(dataFolder.toFile(), "platform-dependent.yml").toPath());
    }

    @Test
    @DisplayName("should leave out an option the platform cannot honour")
    void shouldOmitUnsupportedOption() throws IOException {
        final String yaml = write(Capabilities.of(Capability.TITLE_NOTICE));

        assertThat(yaml).contains("enabled");
        assertThat(yaml).doesNotContain("defaultWorld");
    }

    @Test
    @DisplayName("should write the option where the platform supports it")
    void shouldKeepSupportedOption() throws IOException {
        final String yaml = write(Capabilities.all());

        assertThat(yaml).contains("enabled");
        assertThat(yaml).contains("defaultWorld");
    }

    @Test
    @DisplayName("should reload a filtered config without resurrecting the removed option")
    void shouldSurviveReload() throws IOException {
        final ConfigManager manager = new ConfigManager(
                mock(PluginLogger.class), dataFolder.toFile(), Capabilities.of(Capability.TITLE_NOTICE)
        );

        manager.create(PlatformDependentConfig.class);
        manager.loadAll();
        manager.saveAll();

        final String yaml = Files.readString(new File(dataFolder.toFile(), "platform-dependent.yml").toPath());

        assertThat(yaml).doesNotContain("defaultWorld");
    }
}
