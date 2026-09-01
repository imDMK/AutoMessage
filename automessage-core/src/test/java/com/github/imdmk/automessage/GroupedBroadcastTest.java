package com.github.imdmk.automessage;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.support.RecordingViewer;
import com.github.imdmk.automessage.support.TestPlatform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

// One announcement now reaches everybody who can read the same text from a single render, so
// these check that what each player ends up seeing is still exactly what they saw before.
class GroupedBroadcastTest {

    private static final String MESSAGE = "group-test";

    @TempDir
    Path dataFolder;

    private AutoMessage automessage;
    private TestPlatform platform;

    private void announcement(String code, String body) throws IOException {
        Files.createDirectories(dataFolder.resolve("lang"));
        Files.writeString(dataFolder.resolve("lang/" + code + ".yml"),
                "announcements:\n  " + MESSAGE + ":\n" + body);
    }

    private void onlyMessage() throws IOException {
        Files.writeString(dataFolder.resolve("scheduledMessages.yml"), "messages:\n- name: " + MESSAGE + "\n");
    }

    private void start(RecordingViewer... viewers) {
        this.platform = TestPlatform.fullServer();
        for (final RecordingViewer viewer : viewers) {
            platform.join(viewer);
        }
        this.automessage = new AutoMessage(
                platform, mock(PluginLogger.class), dataFolder.toFile(), ExternalPlaceholderResolver.disabled()
        );
    }

    @AfterEach
    void tearDown() {
        if (automessage != null) {
            automessage.shutdown();
        }
    }

    @Test
    @DisplayName("a title reaches every player in the group, not just the first")
    void everyoneInTheGroupSeesTheTitle() throws IOException {
        onlyMessage();
        announcement("en", "  - title: \"<gold>EVENT\"\n    subtitle: \"<gray>soon\"\n");
        final RecordingViewer steve = RecordingViewer.english("Steve");
        final RecordingViewer alex = RecordingViewer.english("Alex");
        start(steve, alex);

        platform.scheduler().tick();

        // Adventure's own group audience takes a title apart into parts a platform may not
        // implement; getting this wrong drops titles for everybody once two players are online.
        assertThat(steve.titles).hasSize(1);
        assertThat(alex.titles).hasSize(1);
    }

    @Test
    @DisplayName("grouping never hands a player another language's text")
    void eachLanguageKeepsItsOwnText() throws IOException {
        onlyMessage();
        announcement("en", "  - \"<gray>Hello\"\n");
        announcement("pl", "  - \"<gray>Czesc\"\n");
        final RecordingViewer steve = RecordingViewer.english("Steve");
        final RecordingViewer kuba = new RecordingViewer("Kuba", "pl_pl", "world");
        start(steve, kuba);

        platform.scheduler().tick();

        assertThat(steve.chat).containsExactly("Hello");
        assertThat(kuba.chat).containsExactly("Czesc");
    }

    @Test
    @DisplayName("a message that names the player is still built for each of them")
    void viewerPlaceholdersStayPerPlayer() throws IOException {
        onlyMessage();
        announcement("en", "  - \"<gray>Hi {PLAYER}\"\n");
        final RecordingViewer steve = RecordingViewer.english("Steve");
        final RecordingViewer alex = RecordingViewer.english("Alex");
        start(steve, alex);

        platform.scheduler().tick();

        assertThat(steve.chat).containsExactly("Hi Steve");
        assertThat(alex.chat).containsExactly("Hi Alex");
    }

    @Test
    @DisplayName("a placeholder added by a reload is resolved rather than printed raw")
    void reloadIsSeenByThePlaceholderScan() throws IOException {
        onlyMessage();
        announcement("en", "  - \"<gray>Players online\"\n");
        final RecordingViewer steve = RecordingViewer.english("Steve");
        start(steve);

        platform.scheduler().tick();
        assertThat(steve.chat).containsExactly("Players online");

        // Which placeholders a message contains is remembered between announcements. A reload
        // that adds one has to be noticed, or the token reaches the player as literal text.
        announcement("en", "  - \"<gray>Players online: {ONLINE}\"\n");
        automessage.configReloadService().reload();
        steve.forget();

        platform.scheduler().tick();
        assertThat(steve.chat).containsExactly("Players online: 1");
    }
}
