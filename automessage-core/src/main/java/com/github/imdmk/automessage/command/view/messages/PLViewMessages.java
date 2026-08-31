package com.github.imdmk.automessage.command.view.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;

/**
 * Polish text for the /automessage view replies.
 *
 * <p>
 * Field documentation lives on {@link ENViewMessages}, which backs messages.yml - keeping it in one
 * place is why the translated files carry no per-field comments of their own.
 * </p>
 */
public final class PLViewMessages
        extends OkaeriConfig
        implements ViewMessages {

    Notice messagePreviewed = Notice.chat(
            "<dark_gray>• <green>Podgląd zaplanowanej wiadomości <gray>{MESSAGE}<green>.<dark_gray>"
    );

    Notice messageNotFound = Notice.chat(
            "<dark_gray>• <red>Wiadomość o nazwie <gray>{MESSAGE} <red>nie istnieje w scheduledMessages.yml."
    );

    Notice viewPlayerOnly = Notice.chat(
            "<dark_gray>• <red>Tylko gracze mogą oglądać podgląd zaplanowanych wiadomości.<dark_gray>"
    );

    @Override
    public Notice messagePreviewed() {
        return messagePreviewed;
    }

    @Override
    public Notice messageNotFound() {
        return messageNotFound;
    }

    @Override
    public Notice viewPlayerOnly() {
        return viewPlayerOnly;
    }
}
