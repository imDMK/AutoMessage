package com.github.imdmk.automessage.platform.litecommands.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public final class LiteCommandsMessagesImpl
        extends OkaeriConfig
        implements LiteCommandsMessages {

    @Comment({
            "#",
            "# Sent when a player attempts to execute a command without having all required permissions.",
            "#",
            "# Placeholders:",
            "#  {PERMISSIONS} - comma-separated list of missing permission nodes required to execute the command.",
            "#"
    })
    Notice commandPermissionMissing = Notice.chat(
            "<dark_gray>• <red>You are missing required permissions <gray>{PERMISSIONS} <red>to execute this command."
    );

    @Comment({
            "#",
            "# Sent when a player uses a command with an invalid or incomplete syntax.",
            "# ",
            "# Placeholders:",
            "#   {USAGE} - correct usage format of the command (e.g. /playtime <player>).",
            "#"
    })
    Notice commandUsageInvalid = Notice.chat(
            "<dark_gray>• <red>Invalid command usage! <gray>Correct syntax: <red>{USAGE}<dark_gray>."
    );

    @Comment({
            "#",
            "# Header shown before listing available correct usages for a command.",
            "# Typically used together with 'commandUsageEntry' when there are multiple valid variants.",
            "#"
    })
    Notice commandUsageHeader = Notice.chat(
            "<dark_gray>• <red>Correct usage variants:"
    );

    @Comment({
            "#",
            "# Single entry in the list of valid command usages.",
            "# Displayed under 'commandUsageHeader' for each available usage.",
            "# ",
            "# Placeholders:",
            "#  {USAGE} - a single valid usage variant of the command.",
            "#"
    })
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
