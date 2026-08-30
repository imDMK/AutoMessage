package com.github.imdmk.automessage.scheduled.locale;

import java.util.Locale;

/**
 * Normalises the locale strings a client reports and a configuration file contains.
 *
 * <p>
 * Minecraft sends locales as {@code pl_pl} or {@code en_gb}; people write {@code pl}, {@code PL}
 * or {@code pl-PL} in YAML. Both sides are folded to the same shape here so a translation is not
 * silently missed over punctuation.
 * </p>
 */
public final class MessageLocale {

    private MessageLocale() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * @return the locale in lower-case with underscores, e.g. {@code pl_pl}
     */
    public static String normalize(String locale) {
        if (locale == null || locale.isBlank()) {
            return "";
        }

        return locale.trim().toLowerCase(Locale.ROOT).replace('-', '_');
    }

    /**
     * @return just the language part, e.g. {@code pl} for {@code pl_pl}
     */
    public static String language(String locale) {
        final String normalized = normalize(locale);
        final int separator = normalized.indexOf('_');

        return separator < 0 ? normalized : normalized.substring(0, separator);
    }
}
