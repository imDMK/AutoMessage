package com.github.imdmk.automessage.platform.litecommands.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;

/**
 * Polish text for the command framework messages.
 *
 * <p>
 * Field documentation lives on {@link ENLiteCommandsMessages}, which backs messages.yml - keeping it in one
 * place is why the translated files carry no per-field comments of their own.
 * </p>
 */
public final class PLLiteCommandsMessages
        extends OkaeriConfig
        implements LiteCommandsMessages {

    Notice commandPermissionMissing = Notice.chat(
            "<dark_gray>• <red>Nie masz wymaganych uprawnień <gray>{PERMISSIONS}<red>, aby użyć tej komendy."
    );

    Notice commandUsageInvalid = Notice.chat(
            "<dark_gray>• <red>Niepoprawne użycie komendy! <gray>Poprawna składnia: <red>{USAGE}<dark_gray>."
    );

    Notice commandUsageHeader = Notice.chat(
            "<dark_gray>• <red>Poprawne warianty użycia:"
    );

    Notice commandUsageEntry = Notice.chat(
            "<dark_gray>• <red>{USAGE}"
    );

    @Override
    public Notice commandPermissionMissing() {
        return commandPermissionMissing;
    }

    @Override
    public Notice commandUsageInvalid() {
        return commandUsageInvalid;
    }

    @Override
    public Notice commandUsageHeader() {
        return commandUsageHeader;
    }

    @Override
    public Notice commandUsageEntry() {
        return commandUsageEntry;
    }
}
