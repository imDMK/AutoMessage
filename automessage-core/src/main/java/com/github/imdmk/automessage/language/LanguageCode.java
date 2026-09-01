package com.github.imdmk.automessage.language;

import java.util.Locale;

public final class LanguageCode {

    private LanguageCode() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static String normalize(String code) {
        if (code == null || code.isBlank()) {
            return "";
        }

        return code.trim().toLowerCase(Locale.ROOT).replace('-', '_');
    }

    // Built from toString(), never from getLanguage(). Bukkit reports "pl_pl" and Multification
    // wraps it in new Locale("pl_pl"), which puts the whole string in the language field - so
    // getLanguage() answers "pl_pl", never "pl", and every Polish player silently gets English.
    public static String of(Locale locale) {
        return locale == null ? "" : normalize(locale.toString());
    }

    public static String language(String code) {
        final String normalized = normalize(code);
        final int separator = normalized.indexOf('_');

        return separator < 0 ? normalized : normalized.substring(0, separator);
    }
}
