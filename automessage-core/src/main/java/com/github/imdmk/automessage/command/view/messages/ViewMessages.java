package com.github.imdmk.automessage.command.view.messages;

import com.eternalcode.multification.notice.Notice;

public interface ViewMessages {

    Notice messagePreviewed();

    Notice messageNotFound();

    Notice viewPlayerOnly();
}
