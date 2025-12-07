package com.github.imdmk.automessage.command.dispatcher.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public final class DispatcherMessagesImpl extends OkaeriConfig implements DispatcherMessages {

    @Comment({"#", "# Sent when automatic message dispatching is successfully enabled.", "#"})
    Notice dispatcherEnabled = Notice.chat(
            "<dark_gray>• <green>Automatic messages have been <bold>enabled</bold>.<dark_gray>"
    );

    @Comment({"#", "# Sent when a user attempts to enable dispatching, but it is already active.", "#"})
    Notice dispatcherAlreadyEnabled = Notice.chat(
            "<dark_gray>• <yellow>Automatic messages are already enabled.<dark_gray>"
    );

    @Comment({"#", "# Sent when automatic message dispatching is successfully disabled.", "#"})
    Notice dispatcherDisabled = Notice.chat(
            "<dark_gray>• <red>Automatic messages have been <bold>disabled</bold>.<dark_gray>"
    );

    @Comment({"#", "# Sent when a user attempts to disable dispatching, but it is already inactive.", "#"})
    Notice dispatcherAlreadyDisabled = Notice.chat(
            "<dark_gray>• <yellow>Automatic messages are already disabled.<dark_gray>"
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
