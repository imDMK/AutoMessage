package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.locale.MessageLocale;
import com.github.imdmk.automessage.scheduled.locale.MessageTranslation;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public record ScheduledMessage(
        String name,
        @Unmodifiable List<Notice> notices,
        @Unmodifiable List<AudienceRule> rules,
        @Unmodifiable List<MessageTranslation> translations) {

    public ScheduledMessage {
        Objects.requireNonNull(name, "name");

        if (notices.isEmpty()) {
            throw new IllegalArgumentException("notices must contains at least one notice");
        }

        notices = List.copyOf(notices);
        rules = List.copyOf(rules);
        translations = List.copyOf(translations);
    }

    /** A message with no translations reads the same for everyone. */
    public ScheduledMessage(
            String name,
            List<Notice> notices,
            List<AudienceRule> rules
    ) {
        this(name, notices, rules, List.of());
    }

    /**
     * Picks the notices a player of this locale should receive.
     *
     * <p>
     * An exact match wins over a language-only one, so {@code pt_br} is preferred to {@code pt}
     * for a Brazilian client while a Portuguese one still falls back to {@code pt}. A player whose
     * language nobody translated gets the default notices rather than nothing at all.
     * </p>
     *
     * @param locale locale reported by the client, e.g. {@code pl_pl}
     * @return the notices to send
     */
    @Unmodifiable
    public List<Notice> noticesFor(String locale) {
        if (translations.isEmpty()) {
            return notices;
        }

        final String normalized = MessageLocale.normalize(locale);

        for (final MessageTranslation translation : translations) {
            if (translation.locale().equals(normalized)) {
                return translation.notices();
            }
        }

        final String language = MessageLocale.language(locale);

        for (final MessageTranslation translation : translations) {
            if (translation.locale().equals(language)) {
                return translation.notices();
            }
        }

        return notices;
    }
}
