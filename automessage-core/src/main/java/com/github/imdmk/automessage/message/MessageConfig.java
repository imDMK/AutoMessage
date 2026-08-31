package com.github.imdmk.automessage.message;

import com.github.imdmk.automessage.command.dispatcher.messages.DispatcherMessages;
import com.github.imdmk.automessage.command.reload.messages.ReloadMessages;
import com.github.imdmk.automessage.command.view.messages.ViewMessages;
import com.github.imdmk.automessage.platform.litecommands.messages.LiteCommandsMessages;

/**
 * Everything the plugin itself says to a player, in one language.
 *
 * <p>
 * One implementation per shipped language, each backed by its own file. Which one a player gets is
 * decided by {@link MessageConfigRegistry} from the language their client is running, so a server
 * does not have to choose a single language for everyone.
 * </p>
 */
public interface MessageConfig {

    LiteCommandsMessages liteCommandsMessages();

    DispatcherMessages dispatcherMessages();

    ReloadMessages reloadMessages();

    ViewMessages viewMessages();
}
