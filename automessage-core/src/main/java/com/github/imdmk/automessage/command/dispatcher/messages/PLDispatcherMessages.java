package com.github.imdmk.automessage.command.dispatcher.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;

/**
 * Polish text for the enable and disable replies.
 *
 * <p>
 * Field documentation lives on {@link ENDispatcherMessages}, which backs messages.yml - keeping it in one
 * place is why the translated files carry no per-field comments of their own.
 * </p>
 */
public final class PLDispatcherMessages
        extends OkaeriConfig
        implements DispatcherMessages {

    Notice dispatcherEnabled = Notice.chat(
            "<dark_gray>• <green>Automatyczne wiadomości zostały <bold>włączone</bold>.<dark_gray>"
    );

    Notice dispatcherAlreadyEnabled = Notice.chat(
            "<dark_gray>• <yellow>Automatyczne wiadomości są już włączone.<dark_gray>"
    );

    Notice dispatcherDisabled = Notice.chat(
            "<dark_gray>• <red>Automatyczne wiadomości zostały <bold>wyłączone</bold>.<dark_gray>"
    );

    Notice dispatcherAlreadyDisabled = Notice.chat(
            "<dark_gray>• <yellow>Automatyczne wiadomości są już wyłączone.<dark_gray>"
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
