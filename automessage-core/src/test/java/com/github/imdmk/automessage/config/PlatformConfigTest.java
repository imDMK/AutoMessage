package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.capability.Capability;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PlatformConfigTest {

    private static final Capabilities PROXY = Capabilities.of(
            Capability.PERMISSION_RULE,
            Capability.GROUP_RULE,
            Capability.SOUND_NOTICE,
            Capability.BOSSBAR_NOTICE,
            Capability.TITLE_NOTICE,
            Capability.METRICS
    );

    private static final Capabilities EMBEDDED = Capabilities.of(
            Capability.SOUND_NOTICE,
            Capability.BOSSBAR_NOTICE,
            Capability.TITLE_NOTICE
    );

    @TempDir
    Path bukkitFolder;

    @TempDir
    Path proxyFolder;

    @TempDir
    Path embeddedFolder;

    private String scheduledMessages(Path folder, Capabilities capabilities) throws IOException {
        final ConfigManager manager =
                new ConfigManager(mock(PluginLogger.class), folder.toFile(), capabilities);

        manager.create(ScheduledMessagesConfig.class);
        manager.saveAll();

        return Files.readString(new File(folder.toFile(), "scheduledMessages.yml").toPath());
    }

    @Test
    @DisplayName("should ship the full example set on a server that can honour it")
    void shouldShipEveryExampleOnAFullServer() throws IOException {
        final String yaml = scheduledMessages(bukkitFolder, Capabilities.all());

        assertThat(yaml)
                .contains("vote-reminder")
                .contains("newcomer-tip")
                .contains("first-join-welcome");
    }

    @Test
    @DisplayName("should leave out the examples a proxy could never deliver")
    void shouldOmitUndeliverableExamplesOnAProxy() throws IOException {
        final String yaml = scheduledMessages(proxyFolder, PROXY);

        // Nothing about these depends on the platform.
        assertThat(yaml)
                .contains("vote-reminder")
                .contains("vip-perk-reminder");

        // Playtime is a statistic the proxy does not keep; a first join is one it cannot recognise.
        assertThat(yaml)
                .doesNotContain("newcomer-tip")
                .doesNotContain("first-join-welcome");
    }

    @Test
    @DisplayName("should document only the rules and triggers the platform can honour")
    void shouldDocumentOnlyWhatThePlatformCanHonour() throws IOException {
        final String bukkit = scheduledMessages(bukkitFolder, Capabilities.all());
        final String proxy = scheduledMessages(proxyFolder, PROXY);

        // The comment block is what an administrator actually reads; on a full server it
        // documents everything.
        assertThat(bukkit)
                .contains("type: WORLD")
                .contains("type: PLAYTIME")
                .contains("type: FIRST_JOIN")
                .contains("PlaceholderAPI")
                .contains("{WORLD}");

        // On a proxy every one of those is an instruction that cannot be followed.
        assertThat(proxy)
                .doesNotContain("type: WORLD")
                .doesNotContain("type: PLAYTIME")
                .doesNotContain("type: FIRST_JOIN")
                .doesNotContain("PlaceholderAPI")
                .doesNotContain("{WORLD}");

        // What neither depends on stays in both.
        assertThat(proxy).contains("type: PLAYER_COUNT");
    }

    @Test
    @DisplayName("should never leak a directive into a written file")
    void shouldNeverLeakADirective() throws IOException {
        // The markers are how the block is described, not something a reader should ever see.
        assertThat(scheduledMessages(bukkitFolder, Capabilities.all()))
                .doesNotContain("@requires")
                .doesNotContain("@end");
    }

    @Test
    @DisplayName("should leave out permission-gated examples where there are no permissions")
    void shouldOmitPermissionExamplesOnAnEmbeddedServer() throws IOException {
        final String yaml = scheduledMessages(embeddedFolder, EMBEDDED);

        // A rule nobody can satisfy is worse than a missing example: it looks configured.
        assertThat(yaml).doesNotContain("vip-perk-reminder");

        // The plain broadcast needs nothing of the platform, so it still ships.
        assertThat(yaml).contains("vote-reminder");

        // And the documentation stops offering a rule nobody here could satisfy.
        assertThat(yaml)
                .doesNotContain("type: PERMISSION")
                .doesNotContain("type: GROUP");
    }
}
