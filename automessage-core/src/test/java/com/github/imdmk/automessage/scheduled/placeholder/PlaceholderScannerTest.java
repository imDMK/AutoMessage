package com.github.imdmk.automessage.scheduled.placeholder;

import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class PlaceholderScannerTest {

    private static List<List<Notice>> message(Notice... notices) {
        return List.of(List.of(notices));
    }

    private static List<List<Notice>> languages(List<Notice>... perLanguage) {
        return List.of(perLanguage);
    }

    @Test
    @DisplayName("finds built-in tokens in a chat notice")
    void findsBuiltinsInChat() {
        List<List<Notice>> message = message(Notice.chat("Hi {PLAYER}, {ONLINE} players online"));

        assertThat(PlaceholderScanner.builtinsIn(message))
                .containsExactlyInAnyOrder(BuiltinPlaceholder.PLAYER, BuiltinPlaceholder.ONLINE);
    }

    @Test
    @DisplayName("looks inside titles, actionbars and bossbars, not just chat")
    void scansEveryTextCarryingNoticeType() {
        List<List<Notice>> message = message(
                Notice.actionBar("{WORLD}"),
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
        List<List<Notice>> message = message(
                Notice.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0F, 1.0F)
        );

        assertThat(PlaceholderScanner.builtinsIn(message)).isEmpty();
        assertThat(PlaceholderScanner.externalTokensIn(message)).isEmpty();
    }

    @Test
    @DisplayName("collects PlaceholderAPI tokens and de-duplicates them")
    void findsExternalTokens() {
        List<List<Notice>> message = message(
                Notice.chat("%vault_eco_balance% and %server_tps%"),
                Notice.actionBar("%server_tps% again")
        );

        assertThat(PlaceholderScanner.externalTokensIn(message))
                .containsExactlyInAnyOrder("%vault_eco_balance%", "%server_tps%");
    }

    @Test
    @DisplayName("finds placeholders that appear only in a translation")
    void scansTranslationsToo() {
        // A translated message is what a player of that language actually receives, so a
        // placeholder living only there has to be resolved just the same.
        List<List<Notice>> message = languages(List.of(Notice.chat("Welcome!")), List.of(Notice.chat("Witaj {PLAYER}, gracz nr {ONLINE}")));

        assertThat(PlaceholderScanner.builtinsIn(message))
                .containsExactlyInAnyOrder(BuiltinPlaceholder.PLAYER, BuiltinPlaceholder.ONLINE);
    }

    @Test
    @DisplayName("finds PlaceholderAPI tokens that appear only in a translation")
    void scansTranslationsForExternalTokens() {
        List<List<Notice>> message = languages(List.of(Notice.chat("Your balance")), List.of(Notice.chat("Twoje saldo: %vault_eco_balance%")));

        assertThat(PlaceholderScanner.externalTokensIn(message))
                .containsExactly("%vault_eco_balance%");
    }

    @Test
    @DisplayName("a message without placeholders yields nothing to resolve")
    void plainMessageHasNoTokens() {
        List<List<Notice>> message = message(Notice.chat("<red>Just a plain announcement"));

        assertThat(PlaceholderScanner.builtinsIn(message)).isEmpty();
        assertThat(PlaceholderScanner.externalTokensIn(message)).isEmpty();
        assertThat(MessagePlaceholders.scan(
                message,
                com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver.disabled()
        ).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("without a viewer, server values resolve and viewer values drop out")
    void resolvesWhatItCanWithoutAViewer() {
        ViewerRegistry viewers = new ViewerRegistry() {

            @Override
            public java.util.Collection<Viewer> online() {
                return List.of();
            }

            @Override
            public int onlineCount() {
                return 0;
            }

            @Override
            public int maxPlayers() {
                return 100;
            }
        };

        List<List<Notice>> message = message(Notice.chat("{ONLINE}/{MAX_PLAYERS} - hi {PLAYER} in {WORLD}"));

        Map<String, String> resolved = MessagePlaceholders
                .scan(message, ExternalPlaceholderResolver.disabled())
                .resolveWithoutViewer(viewers);

        assertThat(resolved).containsEntry("{ONLINE}", "0").containsEntry("{MAX_PLAYERS}", "100");

        // Nothing here can answer "which player", so those resolve to nothing rather than to a
        // raw token a Discord reader would see as breakage.
        assertThat(resolved).containsEntry("{PLAYER}", "").containsEntry("{WORLD}", "");
    }

    @Test
    @DisplayName("every builtin declares whether it needs a viewer")
    void scopesAreDeclared() {
        assertThat(BuiltinPlaceholder.ONLINE.requiresViewer()).isFalse();
        assertThat(BuiltinPlaceholder.MAX_PLAYERS.requiresViewer()).isFalse();
        assertThat(BuiltinPlaceholder.DATE.requiresViewer()).isFalse();
        assertThat(BuiltinPlaceholder.TIME.requiresViewer()).isFalse();

        assertThat(BuiltinPlaceholder.PLAYER.requiresViewer()).isTrue();
        assertThat(BuiltinPlaceholder.DISPLAY_NAME.requiresViewer()).isTrue();
        assertThat(BuiltinPlaceholder.UUID.requiresViewer()).isTrue();
        assertThat(BuiltinPlaceholder.WORLD.requiresViewer()).isTrue();
    }

    @Test
    @DisplayName("asking a viewer-scoped placeholder to resolve without one is a programming error")
    void viewerScopedRefusesToResolveWithoutAViewer() {
        assertThatThrownBy(() -> BuiltinPlaceholder.PLAYER.resolveForServer(null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("external tokens are left alone when PlaceholderAPI is absent")
    void externalTokensSkippedWithoutPapi() {
        List<List<Notice>> message = message(Notice.chat("%vault_eco_balance%"));

        MessagePlaceholders placeholders = MessagePlaceholders.scan(
                message,
                com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver.disabled()
        );

        assertThat(placeholders.isEmpty()).isTrue();
    }
}
