package com.github.imdmk.automessage.platform.discord;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.NoticePart;
import com.eternalcode.multification.notice.resolver.chat.ChatContent;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Renders a scheduled message into something worth posting in Discord.
 *
 * <p>
 * Only the chat lines are taken. A title, an action bar, a boss bar or a sound has no counterpart
 * in a text channel, and mirroring them would post the same sentence two or three times whenever a
 * message uses several channels to say one thing.
 * </p>
 *
 * <p>
 * The default notices are used rather than any translation: a Discord channel has no single reader
 * whose client language could be consulted. Placeholders describing the server are substituted
 * normally; the ones describing a viewer have no value here and are removed rather than posted as
 * raw tokens.
 * </p>
 */
public final class DiscordMessageRenderer {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();

    private DiscordMessageRenderer() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * @param message      message being announced
     * @param placeholders values to substitute, already resolved for a reader-less destination
     * @return the message as plain text, or empty when it has nothing a text channel can show
     */
    public static String render(ScheduledMessage message, Map<String, String> placeholders) {
        final List<String> lines = new ArrayList<>();

        for (final Notice notice : message.notices()) {
            for (final NoticePart<?> part : notice.parts()) {
                if (part.content() instanceof ChatContent chat) {
                    for (final String line : chat.contents()) {
                        // Substituted after flattening: the tokens are plain text either way, and
                        // a value containing a MiniMessage tag must not be parsed as markup.
                        final String plain = substitute(toPlainText(line), placeholders).strip();

                        if (!plain.isBlank()) {
                            lines.add(plain);
                        }
                    }
                }
            }
        }

        return String.join("\n", lines);
    }

    /**
     * Gradients, hover text and click actions mean nothing in Discord, so the MiniMessage is
     * parsed and then flattened to the words a reader actually sees.
     */
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
