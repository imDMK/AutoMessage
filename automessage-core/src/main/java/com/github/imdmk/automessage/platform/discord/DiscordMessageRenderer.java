package com.github.imdmk.automessage.platform.discord;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.NoticePart;
import com.eternalcode.multification.notice.resolver.chat.ChatContent;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;

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
 * whose client language could be consulted. Placeholders are likewise left as written, because the
 * ones that mean anything - the viewer's name, their world - have no value without a viewer.
 * </p>
 */
public final class DiscordMessageRenderer {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();

    private DiscordMessageRenderer() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * @return the message as plain text, or empty when it has nothing a text channel can show
     */
    public static String render(ScheduledMessage message) {
        final List<String> lines = new ArrayList<>();

        for (final Notice notice : message.notices()) {
            for (final NoticePart<?> part : notice.parts()) {
                if (part.content() instanceof ChatContent chat) {
                    for (final String line : chat.contents()) {
                        final String plain = toPlainText(line);

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
}
