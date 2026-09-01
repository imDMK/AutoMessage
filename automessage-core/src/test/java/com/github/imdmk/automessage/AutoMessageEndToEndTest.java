package com.github.imdmk.automessage;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.support.RecordingViewer;
import com.github.imdmk.automessage.support.TestPlatform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

// The plugin as a whole, on a server that only exists in this test.
//
// Every other test here checks one piece. This one builds the real AutoMessage from a real
// configuration and drives a real broadcast all the way to what a player would see - which is the
// only place the wiring between those pieces is exercised at all.
class AutoMessageEndToEndTest {

    @TempDir
    Path dataFolder;

    private TestPlatform platform;
    private AutoMessage automessage;

    private AutoMessage start(TestPlatform platform) {
        this.platform = platform;
        this.automessage = new AutoMessage(
                platform,
                mock(PluginLogger.class),
                dataFolder.toFile(),
                ExternalPlaceholderResolver.disabled()
        );

        return automessage;
    }

    @AfterEach
    void tearDown() {
        if (automessage != null) {
            automessage.shutdown();
        }
    }

    @Test
    @DisplayName("should write a usable configuration and schedule the channel it declares")
    void shouldStartUp() {
        start(TestPlatform.fullServer());

        assertThat(new File(dataFolder.toFile(), "config.yml")).exists();
        assertThat(new File(dataFolder.toFile(), "scheduledMessages.yml")).exists();
        assertThat(new File(dataFolder.toFile(), "lang/en.yml")).exists();

        // One channel ships by default, so exactly one repeating task should be running.
        assertThat(platform.scheduler().runningTimers()).isEqualTo(1);
    }

    @Test
    @DisplayName("should deal every shipped announcement once before repeating any")
    void shouldBroadcastTheWholeRotation() {
        final RecordingViewer steve = RecordingViewer.english("Steve", "rank.vip");
        start(TestPlatform.fullServer().join(steve));

        // Eight of the nine shipped messages are on the rotation; the ninth waits for a first
        // join. SHUFFLE deals a whole deck, so eight ticks is exactly one of each.
        for (int tick = 0; tick < 8; tick++) {
            platform.scheduler().tick();
        }

        assertThat(steve.everythingSeen())
                .hasSize(9)
                .doesNotHaveDuplicates();

        // A sound rides along with the vote reminder, which is how we know a multi-part notice
        // survived the whole chain.
        assertThat(steve.sounds).hasSize(1);
    }

    @Test
    @DisplayName("should substitute placeholders with values from this server")
    void shouldSubstitutePlaceholders() {
        final RecordingViewer steve = RecordingViewer.english("Steve");
        start(TestPlatform.fullServer().join(steve));

        for (int tick = 0; tick < 8; tick++) {
            platform.scheduler().tick();
        }

        assertThat(steve.chat)
                .anySatisfy(line -> assertThat(line).contains("There are 1/" + TestPlatform.MAX_PLAYERS));

        assertThat(steve.everythingSeen()).noneMatch(line -> line.contains("{"));
    }

    @Test
    @DisplayName("should serve each player the language their client reports")
    void shouldServeEachPlayerTheirLanguage() {
        final RecordingViewer english = RecordingViewer.english("Steve");
        final RecordingViewer polish = new RecordingViewer("Kuba", "pl_pl", "world");

        start(TestPlatform.fullServer().join(english).join(polish));

        for (int tick = 0; tick < 8; tick++) {
            platform.scheduler().tick();
        }

        assertThat(english.chat).anySatisfy(line -> assertThat(line).contains("Enjoying the server"));
        assertThat(polish.chat).anySatisfy(line -> assertThat(line).contains("Podoba Ci"));

        // The same broadcast, two languages: nobody sees the other's text.
        assertThat(polish.chat).noneMatch(line -> line.contains("Enjoying the server"));
    }

    @Test
    @DisplayName("should keep a permission-gated announcement from whoever lacks the permission")
    void shouldHonourAudienceRules() {
        final RecordingViewer vip = RecordingViewer.english("Vip", "rank.vip");
        final RecordingViewer plain = RecordingViewer.english("Plain");

        start(TestPlatform.fullServer().join(vip).join(plain));

        for (int tick = 0; tick < 8; tick++) {
            platform.scheduler().tick();
        }

        assertThat(vip.chat).anySatisfy(line -> assertThat(line).contains("VIP tip"));
        assertThat(plain.chat).noneMatch(line -> line.contains("VIP tip"));
    }

    @Test
    @DisplayName("should greet a first-time player and nobody else")
    void shouldGreetAFirstJoin() {
        final RecordingViewer newcomer = RecordingViewer.english("Newcomer");
        final RecordingViewer regular = RecordingViewer.english("Regular");

        start(TestPlatform.fullServer().join(newcomer).join(regular));

        automessage.triggerService().onJoin(newcomer, true);
        automessage.triggerService().onJoin(regular, false);

        // The welcome waits three seconds so the join spam settles first.
        platform.scheduler().runDelayed();

        assertThat(newcomer.chat).anySatisfy(line -> assertThat(line).contains("Welcome to the server, Newcomer"));
        assertThat(regular.chat).isEmpty();
    }

    @Test
    @DisplayName("should not greet somebody who left while the greeting was waiting")
    void shouldNotGreetAPlayerWhoLeft() {
        final RecordingViewer newcomer = RecordingViewer.english("Newcomer");
        start(TestPlatform.fullServer().join(newcomer));

        automessage.triggerService().onJoin(newcomer, true);
        newcomer.disconnect();

        platform.scheduler().runDelayed();

        assertThat(newcomer.chat).isEmpty();
    }

    @Test
    @DisplayName("should apply an edited configuration on reload, without restarting")
    void shouldApplyAnEditedConfigurationOnReload() throws IOException {
        final RecordingViewer steve = RecordingViewer.english("Steve");
        start(TestPlatform.fullServer().join(steve));

        final Path messages = dataFolder.resolve("scheduledMessages.yml");
        Files.writeString(messages, """
                messages:
                  - name: only-one
                """);

        Files.writeString(dataFolder.resolve("lang/en.yml"), """
                announcements:
                  only-one:
                    - "<gray>The only message left"
                """);

        automessage.configReloadService().reload();
        platform.scheduler().tick();

        assertThat(steve.chat).containsExactly("The only message left");
    }

    @Test
    @DisplayName("should stop everything it started when the server shuts down")
    void shouldStopEverythingOnShutdown() {
        final RecordingViewer steve = RecordingViewer.english("Steve");
        start(TestPlatform.fullServer().join(steve));

        automessage.shutdown();
        this.automessage = null;

        assertThat(platform.scheduler().isShutdown()).isTrue();
        assertThat(platform.scheduler().runningTimers()).isZero();

        steve.forget();
        platform.scheduler().tick();

        assertThat(steve.everythingSeen()).isEmpty();
    }

    @Test
    @DisplayName("should shut down cleanly even when the configuration cannot be written")
    void shouldShutDownWithAnUnwritableConfiguration() {
        start(TestPlatform.fullServer());

        // A read-only file, not a read-only folder: the folder permission does not stop a write
        // to a file that already exists, so a test built on it passes without ever failing.
        final File config = new File(dataFolder.toFile(), "config.yml");
        assertThat(config.setReadOnly()).isTrue();

        try {
            assertThatCode(() -> automessage.shutdown()).doesNotThrowAnyException();

            // The point of the fix: the steps after the failing save still ran, so nothing is
            // left holding threads under a class loader nobody can collect.
            assertThat(platform.scheduler().isShutdown()).isTrue();
        } finally {
            assertThat(config.setWritable(true)).isTrue();
            this.automessage = null;
        }
    }

    @Test
    @DisplayName("should send nothing at all while nobody is online")
    void shouldSendNothingToAnEmptyServer() {
        start(TestPlatform.fullServer());

        assertThatCode(() -> {
            for (int tick = 0; tick < 8; tick++) {
                platform.scheduler().tick();
            }
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should leave out what the platform cannot do, all the way through")
    void shouldRespectPlatformCapabilities() {
        final RecordingViewer steve = new RecordingViewer("Steve", "en_us", null);

        // A proxy: no worlds, no playtime, no first join, and no permissions of its own.
        start(new TestPlatform(com.github.imdmk.automessage.platform.capability.Capabilities.of(
                com.github.imdmk.automessage.platform.capability.Capability.SOUND_NOTICE,
                com.github.imdmk.automessage.platform.capability.Capability.BOSSBAR_NOTICE,
                com.github.imdmk.automessage.platform.capability.Capability.TITLE_NOTICE
        )).join(steve));

        for (int tick = 0; tick < 8; tick++) {
            platform.scheduler().tick();
        }

        final List<String> seen = steve.everythingSeen();

        // The examples that need a capability this platform lacks were never written to the file,
        // so they cannot reach anybody.
        assertThat(seen).isNotEmpty();
        assertThat(seen).noneMatch(line -> line.contains("VIP tip"));
        assertThat(seen).noneMatch(line -> line.contains("New here?"));
    }
}
