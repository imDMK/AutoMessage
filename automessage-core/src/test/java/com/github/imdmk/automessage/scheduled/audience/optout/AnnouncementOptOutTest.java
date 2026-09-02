package com.github.imdmk.automessage.scheduled.audience.optout;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AnnouncementOptOutTest {

    private static final UUID STEVE = UUID.randomUUID();
    private static final UUID ALEX = UUID.randomUUID();

    @TempDir
    Path dataFolder;

    private AnnouncementOptOut load() {
        return AnnouncementOptOut.load(mock(PluginLogger.class), dataFolder.toFile());
    }

    private Path file() {
        return dataFolder.resolve("data").resolve("muted.txt");
    }

    @Test
    @DisplayName("nobody is muted until somebody asks to be")
    void nobodyIsMutedToStartWith() {
        final AnnouncementOptOut optOut = load();

        assertThat(optOut.isMuted(STEVE)).isFalse();
        assertThat(optOut.mutedCount()).isZero();
    }

    @Test
    @DisplayName("toggling answers with the state the player ends in")
    void togglingAnswersWithTheResultingState() {
        final AnnouncementOptOut optOut = load();

        assertThat(optOut.toggle(STEVE)).isTrue();
        assertThat(optOut.isMuted(STEVE)).isTrue();

        assertThat(optOut.toggle(STEVE)).isFalse();
        assertThat(optOut.isMuted(STEVE)).isFalse();
    }

    @Test
    @DisplayName("one player asking says nothing about another")
    void mutingOnePlayerLeavesOthersAlone() {
        final AnnouncementOptOut optOut = load();
        optOut.toggle(STEVE);

        assertThat(optOut.isMuted(ALEX)).isFalse();
    }

    @Test
    @DisplayName("the choice survives a restart")
    void theChoiceSurvivesARestart() {
        final AnnouncementOptOut before = load();
        before.toggle(STEVE);
        before.save();

        // The whole point of a file rather than a set in memory: a player who turned
        // announcements off should not have them back after the next restart.
        final AnnouncementOptOut after = load();
        assertThat(after.isMuted(STEVE)).isTrue();
        assertThat(after.isMuted(ALEX)).isFalse();
    }

    @Test
    @DisplayName("turning them back on is written out too")
    void turningBackOnIsPersisted() {
        final AnnouncementOptOut before = load();
        before.toggle(STEVE);
        before.save();
        before.toggle(STEVE);
        before.save();

        assertThat(load().isMuted(STEVE)).isFalse();
    }

    @Test
    @DisplayName("a line that is not a player id costs only that line")
    void anUnreadableLineIsSkipped() throws IOException {
        Files.createDirectories(file().getParent());
        Files.write(file(), List.of(STEVE.toString(), "not-a-uuid", "", ALEX.toString()));

        final AnnouncementOptOut optOut = load();

        assertThat(optOut.isMuted(STEVE)).isTrue();
        assertThat(optOut.isMuted(ALEX)).isTrue();
        assertThat(optOut.mutedCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("writing leaves no half-written file behind")
    void writingLeavesNoTemporaryFile() {
        final AnnouncementOptOut optOut = load();
        optOut.toggle(STEVE);
        optOut.save();

        // Written beside the file and moved over it, so a server that dies mid-write keeps the
        // previous list rather than half of a new one.
        assertThat(file()).exists();
        assertThat(file().resolveSibling("muted.txt.tmp")).doesNotExist();
    }

    @Test
    @DisplayName("a server where nobody asked for quiet grows no file")
    void savingNothingWritesNothing() {
        load().save();

        // A file saying that nobody is muted carries no more than its absence does.
        assertThat(file()).doesNotExist();
    }

    @Test
    @DisplayName("a change that could not be written is tried again next time")
    void anUnwritableChangeIsRetried() throws IOException {
        final AnnouncementOptOut optOut = load();
        optOut.toggle(STEVE);

        // The parent path taken by a file, so creating the directory fails.
        Files.createDirectories(dataFolder);
        Files.write(dataFolder.resolve("data"), List.of("not a directory"));
        optOut.save();

        Files.delete(dataFolder.resolve("data"));
        optOut.save();

        assertThat(load().isMuted(STEVE)).isTrue();
    }
}
