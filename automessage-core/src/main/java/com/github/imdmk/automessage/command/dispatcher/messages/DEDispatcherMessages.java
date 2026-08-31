package com.github.imdmk.automessage.command.dispatcher.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;

/**
 * German text for the enable and disable replies.
 *
 * <p>
 * Field documentation lives on {@link ENDispatcherMessages}, which backs messages.yml - keeping it in one
 * place is why the translated files carry no per-field comments of their own.
 * </p>
 */
public final class DEDispatcherMessages
        extends OkaeriConfig
        implements DispatcherMessages {

    Notice dispatcherEnabled = Notice.chat(
            "<dark_gray>• <green>Automatische Nachrichten wurden <bold>aktiviert</bold>.<dark_gray>"
    );

    Notice dispatcherAlreadyEnabled = Notice.chat(
            "<dark_gray>• <yellow>Automatische Nachrichten sind bereits aktiviert.<dark_gray>"
    );

    Notice dispatcherDisabled = Notice.chat(
            "<dark_gray>• <red>Automatische Nachrichten wurden <bold>deaktiviert</bold>.<dark_gray>"
    );

    Notice dispatcherAlreadyDisabled = Notice.chat(
            "<dark_gray>• <yellow>Automatische Nachrichten sind bereits deaktiviert.<dark_gray>"
    );

    @Override
    public Notice dispatcherEnabled() {
        return dispatcherEnabled;
    }

    @Override
    public Notice dispatcherAlreadyEnabled() {
        return dispatcherAlreadyEnabled;
    }

    @Override
    public Notice dispatcherDisabled() {
        return dispatcherDisabled;
    }

    @Override
    public Notice dispatcherAlreadyDisabled() {
        return dispatcherAlreadyDisabled;
    }
}
