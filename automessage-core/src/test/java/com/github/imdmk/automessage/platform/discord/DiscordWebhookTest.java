package com.github.imdmk.automessage.platform.discord;

import com.github.imdmk.automessage.notice.Notice;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DiscordWebhookTest {

    private static List<Notice> message(Notice... notices) {
        return List.of(notices);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://discord.com/api/webhooks/123/abc",
            "https://discordapp.com/api/webhooks/123/abc",
            "https://ptb.discord.com/api/webhooks/123/abc",
    })
    @DisplayName("accepts a real Discord webhook address")
    void acceptsDiscordWebhooks(String url) {
        assertThat(DiscordWebhookUrl.parse(url)).isPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "http://discord.com/api/webhooks/123/abc",
            "https://evil.example.com/api/webhooks/123/abc",
            "https://discord.com/channels/123",
            "https://discord.com.evil.example/api/webhooks/1/a",
            "not a url at all",
    })
    @DisplayName("rejects anything that is not a Discord webhook over HTTPS")
    void rejectsEverythingElse(String url) {
        // The URL decides where announcements are sent; a typo must not quietly post elsewhere.
        assertThat(DiscordWebhookUrl.parse(url)).isEmpty();
    }

    @Test
    @DisplayName("rejects a null URL")
    void rejectsNull() {
        assertThat(DiscordWebhookUrl.parse(null)).isEmpty();
    }

    @Test
    @DisplayName("mirrors chat lines and flattens MiniMessage to plain text")
    void rendersChatAsPlainText() {
        List<Notice> message = message(
                Notice.chat("<gradient:#ff0000:#00ff00><bold>Vote</bold></gradient> <gray>for rewards!")
        );

        assertThat(DiscordMessageRenderer.render(message, Map.of())).isEqualTo("Vote for rewards!");
    }

    @Test
    @DisplayName("skips notice types a text channel cannot show")
    void skipsNonChatNotices() {
        List<Notice> message = message(
                Notice.actionBar("<red>action bar"),
                Notice.title("<red>title", "<gray>subtitle"),
                Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(1), "boss"),
                Notice.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0F, 1.0F)
        );

        // Mirroring these would repeat one sentence several times per announcement.
        assertThat(DiscordMessageRenderer.render(message, Map.of())).isEmpty();
    }

    @Test
    @DisplayName("joins several chat lines with newlines")
    void joinsChatLines() {
        List<Notice> message = message(Notice.chat("<red>first", "<gray>second"));

        assertThat(DiscordMessageRenderer.render(message, Map.of())).isEqualTo("first\nsecond");
    }

    @Test
    @DisplayName("substitutes server placeholders and drops the viewer ones")
    void substitutesResolvedPlaceholders() {
        List<Notice> message = message(Notice.chat("<gray>{ONLINE}/{MAX_PLAYERS} online, hi {PLAYER}"));

        // A viewer-scoped placeholder resolves to nothing here; posting "{PLAYER}" raw would
        // only tell the reader that something is broken.
        String rendered = DiscordMessageRenderer.render(
                message,
                Map.of("{ONLINE}", "47", "{MAX_PLAYERS}", "100", "{PLAYER}", "")
        );

        assertThat(rendered).isEqualTo("47/100 online, hi");
    }

    @Test
    @DisplayName("leaves a message alone when there is nothing to substitute")
    void withoutPlaceholdersNothingChanges() {
        assertThat(DiscordMessageRenderer.substitute("plain text", Map.of())).isEqualTo("plain text");
    }

    @Test
    @DisplayName("escapes the characters that would break the JSON body")
    void escapesJson() {
        String json = DiscordPayload.build("say \"hi\"\nback\\slash", "bot", "");

        assertThat(json).contains("\\\"hi\\\"").contains("\\n").contains("\\\\slash");
        assertThat(json).contains("\"username\":\"bot\"");
        assertThat(json).doesNotContain("avatar_url");
    }

    @Test
    @DisplayName("never allows an announcement to ping a whole guild")
    void suppressesMentions() {
        assertThat(DiscordPayload.build("@everyone hello", "bot", ""))
                .contains("\"allowed_mentions\":{\"parse\":[]}");
    }

    @Test
    @DisplayName("truncates a body longer than Discord accepts")
    void truncatesOverlongContent() {
        String tooLong = "x".repeat(DiscordPayload.CONTENT_LIMIT + 500);

        assertThat(DiscordPayload.truncate(tooLong)).hasSize(DiscordPayload.CONTENT_LIMIT);
    }

    @Test
    @DisplayName("escapes control characters as unicode")
    void escapesControlCharacters() {
        String withControlCharacter = "a" + (char) 1 + "b";

        assertThat(DiscordPayload.escape(withControlCharacter)).isEqualTo("a\\u0001b");
    }
}
