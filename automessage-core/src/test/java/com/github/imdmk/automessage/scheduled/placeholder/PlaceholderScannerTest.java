package com.github.imdmk.automessage.scheduled.placeholder;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageBuilder;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlaceholderScannerTest {

    private static ScheduledMessage message(Notice... notices) {
        return new ScheduledMessage("test", List.of(notices), List.of());
    }

    @Test
    @DisplayName("finds built-in tokens in a chat notice")
    void findsBuiltinsInChat() {
        ScheduledMessage message = message(Notice.chat("Hi {PLAYER}, {ONLINE} players online"));

        assertThat(PlaceholderScanner.builtinsIn(message))
                .containsExactlyInAnyOrder(BuiltinPlaceholder.PLAYER, BuiltinPlaceholder.ONLINE);
    }

    @Test
    @DisplayName("looks inside titles, actionbars and bossbars, not just chat")
    void scansEveryTextCarryingNoticeType() {
        ScheduledMessage message = message(
                Notice.actionbar("{WORLD}"),
                Notice.title("{DATE}", "{TIME}"),
                Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(1), "{MAX_PLAYERS}")
        );

        assertThat(PlaceholderScanner.builtinsIn(message)).containsExactlyInAnyOrder(
                BuiltinPlaceholder.WORLD,
                BuiltinPlaceholder.DATE,
                BuiltinPlaceholder.TIME,
                BuiltinPlaceholder.MAX_PLAYERS
        );
    }

    @Test
    @DisplayName("a sound notice carries no text and contributes no tokens")
    void soundNoticesAreIgnored() {
        ScheduledMessage message = message(
                Notice.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0F, 1.0F)
        );

        assertThat(PlaceholderScanner.builtinsIn(message)).isEmpty();
        assertThat(PlaceholderScanner.externalTokensIn(message)).isEmpty();
    }

    @Test
    @DisplayName("collects PlaceholderAPI tokens and de-duplicates them")
    void findsExternalTokens() {
        ScheduledMessage message = message(
                Notice.chat("%vault_eco_balance% and %server_tps%"),
                Notice.actionbar("%server_tps% again")
        );

        assertThat(PlaceholderScanner.externalTokensIn(message))
                .containsExactlyInAnyOrder("%vault_eco_balance%", "%server_tps%");
    }

    @Test
    @DisplayName("finds placeholders that appear only in a translation")
    void scansTranslationsToo() {
        // A translated message is what a player of that language actually receives, so a
        // placeholder living only there has to be resolved just the same.
        ScheduledMessage message = ScheduledMessageBuilder.create()
                .name("greeting")
                .addNotice(Notice.chat("Welcome!"))
                .addTranslation("pl", Notice.chat("Witaj {PLAYER}, gracz nr {ONLINE}"))
                .build();

        assertThat(PlaceholderScanner.builtinsIn(message))
                .containsExactlyInAnyOrder(BuiltinPlaceholder.PLAYER, BuiltinPlaceholder.ONLINE);
    }

    @Test
    @DisplayName("finds PlaceholderAPI tokens that appear only in a translation")
    void scansTranslationsForExternalTokens() {
        ScheduledMessage message = ScheduledMessageBuilder.create()
                .name("balance")
                .addNotice(Notice.chat("Your balance"))
                .addTranslation("pl", Notice.chat("Twoje saldo: %vault_eco_balance%"))
                .build();

        assertThat(PlaceholderScanner.externalTokensIn(message))
                .containsExactly("%vault_eco_balance%");
    }

    @Test
    @DisplayName("a message without placeholders yields nothing to resolve")
    void plainMessageHasNoTokens() {
        ScheduledMessage message = message(Notice.chat("<red>Just a plain announcement"));

        assertThat(PlaceholderScanner.builtinsIn(message)).isEmpty();
        assertThat(PlaceholderScanner.externalTokensIn(message)).isEmpty();
        assertThat(MessagePlaceholders.scan(
                message,
                com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver.disabled()
        ).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("external tokens are left alone when PlaceholderAPI is absent")
    void externalTokensSkippedWithoutPapi() {
        ScheduledMessage message = message(Notice.chat("%vault_eco_balance%"));

        MessagePlaceholders placeholders = MessagePlaceholders.scan(
                message,
                com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver.disabled()
        );

        assertThat(placeholders.isEmpty()).isTrue();
    }
}
