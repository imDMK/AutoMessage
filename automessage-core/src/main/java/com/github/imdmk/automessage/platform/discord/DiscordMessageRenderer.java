package com.github.imdmk.automessage.platform.discord;

import com.github.imdmk.automessage.notice.Notice;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DiscordMessageRenderer {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();

    private DiscordMessageRenderer() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static String render(List<Notice> notices, Map<String, String> placeholders) {
        final List<String> lines = new ArrayList<>();

        for (final Notice notice : notices) {
            for (final String line : notice.chatTexts()) {
                // Substituted after flattening: the tokens are plain text either way, and a value
                // containing a MiniMessage tag must not be parsed as markup.
                final String plain = substitute(toPlainText(line), placeholders).strip();

                if (!plain.isBlank()) {
                    lines.add(plain);
                }
            }
        }

        return String.join("\n", lines);
    }

    static String toPlainText(String miniMessage) {
        return PLAIN_TEXT.serialize(MINI_MESSAGE.deserialize(miniMessage));
    }

    static String substitute(String text, Map<String, String> placeholders) {
        String substituted = text;

        for (final Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            substituted = substituted.replace(placeholder.getKey(), placeholder.getValue());
        }

        return substituted;
    }
}
