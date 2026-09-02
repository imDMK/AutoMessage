package com.github.imdmk.automessage;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.scheduled.dispatcher.ChannelPreview;
import com.github.imdmk.automessage.support.RecordingViewer;
import com.github.imdmk.automessage.support.TestPlatform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

// /automessage next answers what is coming, so what it says has to be what arrives - and asking
// must not itself move the rotation on.
class UpcomingPreviewTest {

    @TempDir
    Path dataFolder;

    private TestPlatform platform;
    private AutoMessage automessage;
    private final RecordingViewer steve = RecordingViewer.english("Steve");

    private void channels(String yaml) throws IOException {
        Files.writeString(dataFolder.resolve("config.yml"),
                "enabled: true\nfallbackLanguage: en\nchannels:\n" + yaml);
    }

    private static String channel(String name, boolean enabled, String selector) {
        return "  - name: " + name + "\n    enabled: " + enabled
                + "\n    initialDelay: 0s\n    period: 1m\n    selector: " + selector + "\n";
    }

    // Each message says its own name, so what a player received names what was chosen.
    private void messages(String... names) throws IOException {
        final StringBuilder scheduled = new StringBuilder("messages:\n");
        final StringBuilder english = new StringBuilder("announcements:\n");

        for (final String name : names) {
            scheduled.append("- name: ").append(name).append('\n');
            english.append("  ").append(name).append(":\n  - \"").append(name).append("\"\n");
        }

        Files.writeString(dataFolder.resolve("scheduledMessages.yml"), scheduled.toString());
        Files.createDirectories(dataFolder.resolve("lang"));
        Files.writeString(dataFolder.resolve("lang/en.yml"), english.toString());
    }

    private void start() {
        this.platform = TestPlatform.fullServer().join(steve);
        this.automessage = new AutoMessage(
                platform, mock(PluginLogger.class), dataFolder.toFile(), ExternalPlaceholderResolver.disabled()
        );
    }

    private List<ChannelPreview> upcoming() {
        return automessage.dispatcherService().upcoming();
    }

    @AfterEach
    void tearDown() {
        if (automessage != null) {
            automessage.shutdown();
        }
    }

    @Test
    @DisplayName("names the message that actually arrives next")
    void predictsWhatArrives() throws IOException {
        messages("alpha", "beta", "gamma");
        channels(channel("default", true, "SEQUENTIAL"));
        start();

        final ChannelPreview preview = upcoming().getFirst();
        assertThat(preview.kind()).isEqualTo(ChannelPreview.Kind.NEXT);

        platform.scheduler().tick();

        assertThat(steve.chat).containsExactly(preview.message());
    }

    @Test
    @DisplayName("asking does not use up the message it names")
    void askingDoesNotAdvanceTheRotation() throws IOException {
        messages("alpha", "beta", "gamma");
        channels(channel("default", true, "SEQUENTIAL"));
        start();

        upcoming();
        upcoming();
        platform.scheduler().tick();

        // Two questions must not skip two messages. Deliberately not three, which with three
        // messages would wrap right back to the front and pass either way.
        assertThat(steve.chat).containsExactly("alpha");
    }

    @Test
    @DisplayName("admits it cannot know on a channel that draws at random")
    void randomIsAdmittedRatherThanGuessed() throws IOException {
        messages("alpha", "beta");
        channels(channel("default", true, "RANDOM"));
        start();

        assertThat(upcoming()).singleElement()
                .extracting(ChannelPreview::kind)
                .isEqualTo(ChannelPreview.Kind.UNPREDICTABLE);
    }

    @Test
    @DisplayName("says which channels are switched off and which have nothing to say")
    void reportsDisabledAndEmptyChannels() throws IOException {
        messages("alpha");
        channels(channel("default", false, "SEQUENTIAL") + channel("ads", true, "SEQUENTIAL"));
        start();

        assertThat(upcoming())
                .extracting(ChannelPreview::channel, ChannelPreview::kind)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("default", ChannelPreview.Kind.DISABLED),
                        org.assertj.core.groups.Tuple.tuple("ads", ChannelPreview.Kind.EMPTY)
                );
    }
}
