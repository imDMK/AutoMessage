package com.github.imdmk.automessage.platform.litecommands.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;

/**
 * German text for the command framework messages.
 *
 * <p>
 * Field documentation lives on {@link ENLiteCommandsMessages}, which backs messages.yml - keeping it in one
 * place is why the translated files carry no per-field comments of their own.
 * </p>
 */
public final class DELiteCommandsMessages
        extends OkaeriConfig
        implements LiteCommandsMessages {

    Notice commandPermissionMissing = Notice.chat(
            "<dark_gray>• <red>Dir fehlen die nötigen Rechte <gray>{PERMISSIONS}<red>, um diesen Befehl zu nutzen."
    );

    Notice commandUsageInvalid = Notice.chat(
            "<dark_gray>• <red>Falsche Verwendung! <gray>Richtige Syntax: <red>{USAGE}<dark_gray>."
    );

    Notice commandUsageHeader = Notice.chat(
            "<dark_gray>• <red>Mögliche Verwendungen:"
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
