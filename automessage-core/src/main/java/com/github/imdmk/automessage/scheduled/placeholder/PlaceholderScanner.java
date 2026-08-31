package com.github.imdmk.automessage.scheduled.placeholder;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.NoticePart;
import com.eternalcode.multification.notice.resolver.text.TextContent;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds the placeholder tokens a message actually contains.
 *
 * <p>
 * Resolving every known placeholder for every player would mean asking the server for its player
 * count, its TPS and the current time on messages that mention none of them. Scanning the message
 * once tells the dispatcher the short list it really has to resolve, which for a typical
 * announcement is empty.
 * </p>
 *
 * <p>
 * Every notice type that carries text — chat, actionbar, title, bossbar — implements
 * {@link TextContent}, so one check covers them all; a sound notice simply contributes nothing.
 * </p>
 */
public final class PlaceholderScanner {

    /** PlaceholderAPI's own syntax, e.g. {@code %vault_eco_balance%}. */
    private static final Pattern EXTERNAL_TOKEN = Pattern.compile("%[a-zA-Z0-9_]+%");

    private PlaceholderScanner() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * @return the built-in placeholders written somewhere in the message
     */
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

    /**
     * @return every {@code %...%} token in the message, left for PlaceholderAPI to interpret
     */
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
            for (final NoticePart<?> part : notice.parts()) {
                if (part.content() instanceof TextContent textContent) {
                    texts.addAll(textContent.contents());
                }
            }
        }
    }
}
