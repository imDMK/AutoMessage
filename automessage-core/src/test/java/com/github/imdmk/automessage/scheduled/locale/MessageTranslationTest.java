package com.github.imdmk.automessage.scheduled.locale;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageTranslationTest {

    private static final Notice DEFAULT_NOTICE = Notice.chat("Vote for rewards!");
    private static final Notice POLISH_NOTICE = Notice.chat("Zaglosuj po nagrody!");
    private static final Notice BRAZILIAN_NOTICE = Notice.chat("Vote por recompensas!");

    private static ScheduledMessage translated() {
        return ScheduledMessageBuilder.create()
                .name("vote")
                .addNotice(DEFAULT_NOTICE)
                .addTranslation("pl", POLISH_NOTICE)
                .addTranslation("pt_br", BRAZILIAN_NOTICE)
                .build();
    }

    @ParameterizedTest(name = "\"{0}\" normalises to \"{1}\"")
    @CsvSource({
            "pl_pl,  pl_pl",
            "PL-PL,  pl_pl",
            "  en_GB  , en_gb",
            "pl,     pl",
    })
    @DisplayName("folds the shapes clients and config files use into one")
    void normalisesLocales(String input, String expected) {
        assertThat(MessageLocale.normalize(input)).isEqualTo(expected);
    }

    @Test
    @DisplayName("extracts the language from a full locale")
    void extractsLanguage() {
        assertThat(MessageLocale.language("pt_br")).isEqualTo("pt");
        assertThat(MessageLocale.language("pl")).isEqualTo("pl");
        assertThat(MessageLocale.language(null)).isEmpty();
    }

    @Test
    @DisplayName("a client language with a translation gets it")
    void matchesOnLanguage() {
        assertThat(translated().noticesFor("pl_pl")).containsExactly(POLISH_NOTICE);
    }

    @Test
    @DisplayName("an exact locale match beats the language-only one")
    void exactMatchWins() {
        // Notice has no equals(), so identity is what the assertions below compare - the same
        // instance has to be handed to the builder and to the expectation.
        Notice portugueseNotice = Notice.chat("Portugal");

        ScheduledMessage message = ScheduledMessageBuilder.create()
                .name("vote")
                .addNotice(DEFAULT_NOTICE)
                .addTranslation("pt", portugueseNotice)
                .addTranslation("pt_br", BRAZILIAN_NOTICE)
                .build();

        assertThat(message.noticesFor("pt_br")).containsExactly(BRAZILIAN_NOTICE);
        assertThat(message.noticesFor("pt_pt")).containsExactly(portugueseNotice);
    }

    @Test
    @DisplayName("an untranslated language falls back to the default notices")
    void fallsBackToDefault() {
        assertThat(translated().noticesFor("de_de")).containsExactly(DEFAULT_NOTICE);
        assertThat(translated().noticesFor(null)).containsExactly(DEFAULT_NOTICE);
        assertThat(translated().noticesFor("")).containsExactly(DEFAULT_NOTICE);
    }

    @Test
    @DisplayName("a message with no translations reads the same for everyone")
    void untranslatedMessagesAreUnaffected() {
        ScheduledMessage message = new ScheduledMessage("plain", List.of(DEFAULT_NOTICE), List.of());

        assertThat(message.noticesFor("pl_pl")).containsExactly(DEFAULT_NOTICE);
        assertThat(message.translations()).isEmpty();
    }

    @Test
    @DisplayName("a translation without notices is a configuration error")
    void rejectsEmptyTranslation() {
        assertThatThrownBy(() -> new MessageTranslation("pl", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
