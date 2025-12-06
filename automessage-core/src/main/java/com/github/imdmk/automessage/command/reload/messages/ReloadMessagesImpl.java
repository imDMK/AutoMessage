package com.github.imdmk.automessage.command.reload.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public final class ReloadMessagesImpl extends OkaeriConfig implements ReloadMessages {

    @Comment({"#", "# Message shown when all configuration files are successfully reloaded.", "#"})
    Notice configReloadedSuccess = Notice.chat(
            "<dark_gray>• <green>AutoMessage configuration has been reloaded successfully.<dark_gray>"
    );

    @Comment({"#", "# Message shown when reloading the configuration fails due to invalid or corrupted files.", "#"})
    Notice configReloadFailed = Notice.chat(
            "<dark_gray>• <red>Failed to reload AutoMessage configuration files. "
                    + "<red>Please disable the plugin and verify your configuration."
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
