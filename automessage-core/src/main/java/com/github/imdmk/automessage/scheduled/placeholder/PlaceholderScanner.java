package com.github.imdmk.automessage.scheduled.placeholder;

import com.github.imdmk.automessage.notice.Notice;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlaceholderScanner {

    private static final Pattern EXTERNAL_TOKEN = Pattern.compile("%[a-zA-Z0-9_]+%");

    private PlaceholderScanner() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static Set<BuiltinPlaceholder> builtinsIn(List<List<Notice>> translations) {
        final Set<BuiltinPlaceholder> found = new LinkedHashSet<>();
        final List<String> texts = textsOf(translations);

        for (final BuiltinPlaceholder placeholder : BuiltinPlaceholder.values()) {
            for (final String text : texts) {
                if (text.contains(placeholder.token())) {
                    found.add(placeholder);
                    break;
                }
            }
        }

        return found;
    }

    public static Set<String> externalTokensIn(List<List<Notice>> translations) {
        final Set<String> found = new LinkedHashSet<>();

        for (final String text : textsOf(translations)) {
            final Matcher matcher = EXTERNAL_TOKEN.matcher(text);

            while (matcher.find()) {
                found.add(matcher.group());
            }
        }

        return found;
    }

    private static List<String> textsOf(List<List<Notice>> translations) {
        final List<String> texts = new java.util.ArrayList<>();

        // Every language, not just the fallback: a placeholder written only in the Polish text
        // still has to be resolved for the players reading it, or it reaches them as the
        // literal token.
        for (final List<Notice> notices : translations) {
            collect(notices, texts);
        }

        return texts;
    }

    private static void collect(List<Notice> notices, List<String> texts) {
        for (final Notice notice : notices) {
            texts.addAll(notice.texts());
        }
    }
}
