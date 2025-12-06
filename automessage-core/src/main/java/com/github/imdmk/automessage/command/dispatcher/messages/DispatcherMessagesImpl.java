package com.github.imdmk.automessage.command.dispatcher.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public final class DispatcherMessagesImpl extends OkaeriConfig implements DispatcherMessages {

    @Comment({"#", "# Message shown when automatic message dispatching gets enabled.", "#"})
    Notice dispatcherEnabled = Notice.chat(
            "<dark_gray>• <green>Automatic messages have been enabled.<dark_gray>"
    );

    @Comment({"#", "# Message shown when automatic message dispatching gets disabled.", "#"})
    Notice dispatcherDisabled = Notice.chat(
            "<dark_gray>• <red>Automatic messages have been disabled.<dark_gray>"
    );

    @Override
    public Notice dispatcherEnabled() {
        return dispatcherEnabled;
    }

    @Override
    public Notice dispatcherDisabled() {
        return dispatcherDisabled;
    }
}
