package com.github.imdmk.automessage.command.reload.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;

/**
 * Polish text for the reload replies.
 *
 * <p>
 * Field documentation lives on {@link ENReloadMessages}, which backs messages.yml - keeping it in one
 * place is why the translated files carry no per-field comments of their own.
 * </p>
 */
public final class PLReloadMessages
        extends OkaeriConfig
        implements ReloadMessages {

    Notice configReloadedSuccess = Notice.chat(
            "<dark_gray>• <green>Konfiguracja AutoMessage została przeładowana pomyślnie.<dark_gray>"
    );

    Notice configReloadFailed = Notice.chat(
            "<dark_gray>• <red>Nie udało się przeładować plików konfiguracyjnych AutoMessage. <red>Wyłącz plugin i sprawdź swoją konfigurację."
    );

    @Override
    public Notice configReloadedSuccess() {
        return configReloadedSuccess;
    }

    @Override
    public Notice configReloadFailed() {
        return configReloadFailed;
    }
}
