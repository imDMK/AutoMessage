package com.github.imdmk.automessage.language;

import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.notice.ActionBarPart;
import com.github.imdmk.automessage.notice.BossBarPart;
import com.github.imdmk.automessage.notice.ChatPart;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.notice.SoundPart;
import com.github.imdmk.automessage.notice.SubtitlePart;
import com.github.imdmk.automessage.notice.TitlePart;
import com.github.imdmk.automessage.notice.TitleTimesPart;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LegacyLanguageFileTest {

    @TempDir
    Path dataFolder;

    private LanguageConfig loadLegacy(String yaml) throws Exception {
        Files.createDirectories(dataFolder.resolve("lang"));
        Files.writeString(dataFolder.resolve("lang/en.yml"), yaml);

        return LanguageRegistry
                .load(new ConfigManager(mock(PluginLogger.class), dataFolder.toFile()),
                        mock(PluginLogger.class), () -> "en")
                .provide("en");
    }

    @Test
    @DisplayName("reads every notice shape the old format could write")
    void readsEveryLegacyShape() throws Exception {
        LanguageConfig language = loadLegacy("""
                commands:
                  viewPlayerOnly: "<red>players only"
                announcements:
                  chat-single:
                  - "<gray>one line"
                  chat-multi:
                  - - <gray>first
                    - <gray>second
                  actionbar:
                  - actionbar: "<yellow>above the hotbar"
                  title-pair:
                  - title: <red>Title
                    subtitle: <gray>Sub
                  title-timed:
                  - title: <red>Title
                    subtitle: <gray>Sub
                    times: "500ms 3s 500ms"
                  bossbar:
                  - bossbar:
                      message: <green>bar
                      duration: 5s
                      color: RED
                      overlay: PROGRESS
                      progress: '0.5'
                  sound-bare:
                  - sound: entity.player.levelup
                  sound-full:
                  - sound: "block.note_block.pling MASTER 2.0 7.0"
                  mixed:
                  - <gray>chat
                  - actionbar: <yellow>bar
                  - sound: "entity.experience_orb.pickup MASTER 1.0 1.0"
                """);

        assertThat(language.announcement("chat-single"))
                .containsExactly(Notice.of(ChatPart.of("<gray>one line")));

        assertThat(language.announcement("chat-multi"))
                .containsExactly(Notice.of(ChatPart.of("<gray>first", "<gray>second")));

        assertThat(language.announcement("actionbar"))
                .containsExactly(Notice.of(new ActionBarPart("<yellow>above the hotbar")));

        assertThat(language.announcement("title-pair"))
                .containsExactly(Notice.of(new TitlePart("<red>Title"), new SubtitlePart("<gray>Sub")));

        assertThat(language.announcement("title-timed").getFirst().parts())
                .contains(new TitleTimesPart(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500)));

        assertThat(language.announcement("bossbar"))
                .containsExactly(Notice.of(new BossBarPart(
                        "<green>bar", Duration.ofSeconds(5), BossBar.Color.RED, BossBar.Overlay.PROGRESS, 0.5D)));

        assertThat(language.announcement("sound-bare"))
                .containsExactly(Notice.of(SoundPart.of(Key.key("entity.player.levelup"))));

        // Volume then pitch, as the file spells it - getting this backwards would swap every
        // configured sound on every server.
        SoundPart sound = (SoundPart) language.announcement("sound-full").getFirst().parts().getFirst();
        assertThat(sound.volumeOrDefault()).isEqualTo(2.0F);
        assertThat(sound.pitchOrDefault()).isEqualTo(7.0F);

        assertThat(language.announcement("mixed")).hasSize(3);
    }

    @Test
    @DisplayName("reads the command messages a server has already customised")
    void readsCustomisedCommandMessages() throws Exception {
        LanguageConfig language = loadLegacy("""
                commands:
                  viewPlayerOnly: "<red>my own wording"
                  dispatcherEnabled: "<green>turned on"
                announcements: {}
                """);

        assertThat(language.commands.viewPlayerOnly.texts()).containsExactly("<red>my own wording");
        assertThat(language.commands.dispatcherEnabled.texts()).containsExactly("<green>turned on");
    }

    @Test
    @DisplayName("a file the server never touched still gets the shipped defaults")
    void keepsShippedDefaultsForUntouchedKeys() throws Exception {
        LanguageConfig language = loadLegacy("""
                commands:
                  viewPlayerOnly: "<red>mine"
                announcements: {}
                """);

        // Only one key was overridden; the rest must still say something.
        assertThat(language.commands.configReloadedSuccess).isNotNull();
        assertThat(language.commands.configReloadedSuccess.texts()).isNotEmpty();
    }
}
