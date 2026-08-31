package com.github.imdmk.automessage.language;

import java.util.Locale;

/**
 * Normalises the several shapes a language code arrives in.
 *
 * <p>
 * Bukkit reports {@code pl_pl}. Multification wraps that in {@code new Locale("pl_pl")}, which
 * puts the whole string in the language field - so {@link Locale#getLanguage()} answers
 * {@code pl_pl}, not {@code pl}. People write {@code pl}, {@code PL} or {@code pl-PL} in file
 * names and configuration. Everything is folded to lower case with an underscore here so the
 * lookup cannot miss over punctuation, which is exactly how the first version of this went wrong.
 * </p>
 */
public final class LanguageCode {

    private LanguageCode() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /** @return the code in lower case with an underscore separator, e.g. {@code pt_br} */
    public static String normalize(String code) {
        if (code == null || code.isBlank()) {
            return "";
        }

        return code.trim().toLowerCase(Locale.ROOT).replace('-', '_');
    }

    /**
     * Reads a code out of a {@link Locale} without trusting its accessors.
     *
     * <p>
     * {@code getLanguage()} cannot be relied on here: for the locales Multification builds from
     * Bukkit it returns the whole {@code pl_pl} string. {@code toString()} gives the same text in
     * both cases, so it is the one to normalise.
     * </p>
     */
    public static String of(Locale locale) {
        return locale == null ? "" : normalize(locale.toString());
    }

    /** @return just the language part: {@code pt} for {@code pt_br} */
    public static String language(String code) {
        final String normalized = normalize(code);
        final int separator = normalized.indexOf('_');

        return separator < 0 ? normalized : normalized.substring(0, separator);
    }
}
