package com.github.imdmk.automessage.message;

import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.command.dispatcher.messages.DEDispatcherMessages;
import com.github.imdmk.automessage.command.reload.messages.DEReloadMessages;
import com.github.imdmk.automessage.command.view.messages.DEViewMessages;
import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.platform.litecommands.messages.DELiteCommandsMessages;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

@Header({
        "# ============================================================================",
        "#                        AutoMessage - messages_de.yml",
        "# ============================================================================",
        "# German translation of messages.yml.",
        "#",
        "# Players whose Minecraft client is set to German read this file; everyone",
        "# else reads messages.yml or the file for their own language.",
        "#",
        "# The field documentation lives in messages.yml, so it is written once rather",
        "# than repeated in every language.",
        "#",
        "# Formatting is MiniMessage. Reload with /automessage reload.",
        "#",
        "# ============================================================================"
})
public final class DEMessageConfig extends ConfigSection implements MessageConfig {

    @Comment({"#", "# Permission errors, usage hints and syntax messages.", "#"})
    public DELiteCommandsMessages liteCommandsMessages = new DELiteCommandsMessages();

    @Comment({"#", "# Replies to /automessage enable and /automessage disable.", "#"})
    public DEDispatcherMessages dispatcherMessages = new DEDispatcherMessages();

    @Comment({"#", "# Replies to /automessage reload.", "#"})
    public DEReloadMessages reloadMessages = new DEReloadMessages();

    @Comment({"#", "# Replies to /automessage view.", "#"})
    public DEViewMessages viewMessages = new DEViewMessages();

    @Override
    public DELiteCommandsMessages liteCommandsMessages() {
        return liteCommandsMessages;
    }

    @Override
    public DEDispatcherMessages dispatcherMessages() {
        return dispatcherMessages;
    }

    @Override
    public DEReloadMessages reloadMessages() {
        return reloadMessages;
    }

    @Override
    public DEViewMessages viewMessages() {
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
        return "messages_de.yml";
    }
}
