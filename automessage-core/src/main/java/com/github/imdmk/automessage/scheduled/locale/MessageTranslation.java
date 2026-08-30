package com.github.imdmk.automessage.scheduled.locale;

import com.eternalcode.multification.notice.Notice;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

/**
 * One message written in one language.
 *
 * @param locale  language this variant is for, e.g. {@code pl} or {@code pt_br}
 * @param notices what that language's players receive instead of the default notices
 */
public record MessageTranslation(
        String locale,
        @Unmodifiable List<Notice> notices
) {

    public MessageTranslation {
        Objects.requireNonNull(locale, "locale");

        if (notices.isEmpty()) {
            throw new IllegalArgumentException(
                    "translation for '" + locale + "' must contain at least one notice"
            );
        }

        locale = MessageLocale.normalize(locale);
        notices = List.copyOf(notices);
    }
}
