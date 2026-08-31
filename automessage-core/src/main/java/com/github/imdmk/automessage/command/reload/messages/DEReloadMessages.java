package com.github.imdmk.automessage.command.reload.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;

/**
 * German text for the reload replies.
 *
 * <p>
 * Field documentation lives on {@link ENReloadMessages}, which backs messages.yml - keeping it in one
 * place is why the translated files carry no per-field comments of their own.
 * </p>
 */
public final class DEReloadMessages
        extends OkaeriConfig
        implements ReloadMessages {

    Notice configReloadedSuccess = Notice.chat(
            "<dark_gray>• <green>Die AutoMessage-Konfiguration wurde erfolgreich neu geladen.<dark_gray>"
    );

    Notice configReloadFailed = Notice.chat(
            "<dark_gray>• <red>Die AutoMessage-Konfigurationsdateien konnten nicht neu geladen werden. <red>Deaktiviere das Plugin und prüfe deine Konfiguration."
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
