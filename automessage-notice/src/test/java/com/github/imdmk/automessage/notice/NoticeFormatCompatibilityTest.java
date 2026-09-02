package com.github.imdmk.automessage.notice;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NoticeFormatCompatibilityTest {

    public static final class Holder extends OkaeriConfig {
        public Map<String, List<Notice>> shapes = new LinkedHashMap<>();
    }

    @TempDir
    Path dir;

    private String writeAndRead(Map<String, List<Notice>> shapes) {
        Path file = dir.resolve("shapes.yml");

        Holder holder = ConfigManager.create(Holder.class, config -> config
                .withConfigurer(new YamlSnakeYamlConfigurer())
                .withSerdesPack(registry -> registry.register(new NoticeSerializer()))
                .withBindFile(file)
                .withRemoveOrphans(true)
                .saveDefaults()
                .load(true));

        holder.shapes = shapes;
        holder.save();

        try {
            return Files.readString(file);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private List<Notice> reload(Map<String, List<Notice>> shapes, String key) {
        writeAndRead(shapes);

        Holder reloaded = ConfigManager.create(Holder.class, config -> config
                .withConfigurer(new YamlSnakeYamlConfigurer())
                .withSerdesPack(registry -> registry.register(new NoticeSerializer()))
                .withBindFile(dir.resolve("shapes.yml"))
                .withRemoveOrphans(true)
                .load(true));

        return reloaded.shapes.get(key);
    }

    private static Map<String, List<Notice>> one(String key, Notice notice) {
        Map<String, List<Notice>> shapes = new LinkedHashMap<>();
        shapes.put(key, List.of(notice));
        return shapes;
    }

    @Test
    @DisplayName("a single chat line is a bare string, exactly as before")
    void chatSingle() {
        assertThat(writeAndRead(one("m", Notice.chat("<gray>one line"))))
                .contains("- <gray>one line");
    }

    @Test
    @DisplayName("several chat lines are a nested list, exactly as before")
    void chatMulti() {
        String yaml = writeAndRead(one("m", Notice.chat("<gray>first", "<gray>second")));

        assertThat(yaml).contains("- - <gray>first").contains("  - <gray>second");
    }

    @Test
    @DisplayName("actionbar, title and subtitle keep their keys")
    void keyedParts() {
        assertThat(writeAndRead(one("m", Notice.actionBar("<yellow>bar")))).contains("actionbar: <yellow>bar");
        assertThat(writeAndRead(one("m", Notice.title("<red>T")))).contains("title: <red>T");
        assertThat(writeAndRead(one("m", Notice.subtitle("<gray>S")))).contains("subtitle: <gray>S");
    }

    @Test
    @DisplayName("a title and its subtitle stay one entry with two keys")
    void titleAndSubtitleShareAnEntry() {
        String yaml = writeAndRead(one("m", Notice.title("<red>T", "<gray>S")));

        assertThat(yaml).contains("- title: <red>T").contains("  subtitle: <gray>S");
    }

    @Test
    @DisplayName("title times stay three durations on one line")
    void titleTimes() {
        Notice notice = Notice.title("<red>T", "<gray>S",
                Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500));

        // Quoting is the emitter's choice, not this serializer's, and YAML reads both the same
        // way - so the assertion is on the value, not on how it is punctuated.
        assertThat(writeAndRead(one("m", notice))).contains("times: 500ms 3s 500ms");
    }

    @Test
    @DisplayName("a bossbar keeps its nested shape and omits an unset progress")
    void bossBar() {
        String withProgress = writeAndRead(one("m", Notice.bossBar(
                BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5), 0.5D, "<green>bar")));

        assertThat(withProgress)
                .contains("- bossbar:")
                .contains("    message: <green>bar")
                .contains("    duration: 5s")
                .contains("    color: RED")
                .contains("    overlay: PROGRESS")
                .contains("    progress: ");

        String withoutProgress = writeAndRead(one("m", Notice.bossBar(
                BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10, Duration.ofSeconds(3), "<green>bar")));

        assertThat(withoutProgress).doesNotContain("progress:");
    }

    @Test
    @DisplayName("a sound with no settings is written as the bare key")
    void bareSound() {
        assertThat(writeAndRead(one("m", Notice.sound(Key.key("entity.player.levelup")))))
                .contains("sound: entity.player.levelup");
    }

    @Test
    @DisplayName("a configured sound keeps the volume-then-pitch order the file has always used")
    void soundOrder() {
        // The order is the easy thing to get backwards, and getting it backwards would swap
        // every configured sound on every server silently.
        Notice notice = Notice.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 2.0F, 7.0F);

        assertThat(writeAndRead(one("m", notice)))
                .contains("sound: block.note_block.pling MASTER 2.0 7.0");
    }

    @Test
    @DisplayName("every shape survives a save and a reload unchanged")
    void roundTrips() {
        Map<String, List<Notice>> shapes = new LinkedHashMap<>();
        shapes.put("all", List.of(
                Notice.chat("<gray>one"),
                Notice.chat("<gray>first", "<gray>second"),
                Notice.actionBar("<yellow>bar"),
                Notice.title("<red>T"),
                Notice.subtitle("<gray>S"),
                Notice.title("<red>T", "<gray>S"),
                Notice.title("<red>T", "<gray>S", Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500)),
                Notice.hideTitle(),
                Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5), 0.5D, "<green>bar"),
                Notice.bossBar(BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10, Duration.ofSeconds(3), "<green>bar"),
                Notice.sound(Key.key("entity.player.levelup")),
                Notice.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 2.0F, 7.0F)
        ));

        assertThat(reload(shapes, "all")).isEqualTo(shapes.get("all"));
    }
}
