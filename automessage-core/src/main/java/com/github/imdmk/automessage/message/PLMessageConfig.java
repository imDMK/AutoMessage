package com.github.imdmk.automessage.message;

import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.command.dispatcher.messages.PLDispatcherMessages;
import com.github.imdmk.automessage.command.reload.messages.PLReloadMessages;
import com.github.imdmk.automessage.command.view.messages.PLViewMessages;
import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.platform.litecommands.messages.PLLiteCommandsMessages;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

@Header({
        "# ============================================================================",
        "#                        AutoMessage - messages_pl.yml",
        "# ============================================================================",
        "# Polish translation of messages.yml.",
        "#",
        "# Players whose Minecraft client is set to Polish read this file; everyone",
        "# else reads messages.yml or the file for their own language.",
        "#",
        "# The field documentation lives in messages.yml, so it is written once rather",
        "# than repeated in every language.",
        "#",
        "# Formatting is MiniMessage. Reload with /automessage reload.",
        "#",
        "# ============================================================================"
})
public final class PLMessageConfig extends ConfigSection implements MessageConfig {

    @Comment({"#", "# Permission errors, usage hints and syntax messages.", "#"})
    public PLLiteCommandsMessages liteCommandsMessages = new PLLiteCommandsMessages();

    @Comment({"#", "# Replies to /automessage enable and /automessage disable.", "#"})
    public PLDispatcherMessages dispatcherMessages = new PLDispatcherMessages();

    @Comment({"#", "# Replies to /automessage reload.", "#"})
    public PLReloadMessages reloadMessages = new PLReloadMessages();

    @Comment({"#", "# Replies to /automessage view.", "#"})
    public PLViewMessages viewMessages = new PLViewMessages();

    @Override
    public PLLiteCommandsMessages liteCommandsMessages() {
        return liteCommandsMessages;
    }

    @Override
    public PLDispatcherMessages dispatcherMessages() {
        return dispatcherMessages;
    }

    @Override
    public PLReloadMessages reloadMessages() {
        return reloadMessages;
    }

    @Override
    public PLViewMessages viewMessages() {
        return viewMessages;
    }

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> registry.register(
                new MultificationSerdesPack(NoticeResolverDefaults.createRegistry())
        );
    }

    @Override
    public String getFileName() {
        return "messages_pl.yml";
    }
}
