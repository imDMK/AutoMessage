package com.github.imdmk.automessage.command.dispatcher.messages;

import com.eternalcode.multification.notice.Notice;

public interface DispatcherMessages {

    Notice dispatcherEnabled();

    Notice dispatcherAlreadyEnabled();

    Notice dispatcherDisabled();

    Notice dispatcherAlreadyDisabled();
}
