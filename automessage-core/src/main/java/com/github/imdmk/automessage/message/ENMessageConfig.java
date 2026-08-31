package com.github.imdmk.automessage.message;

import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.command.dispatcher.messages.ENDispatcherMessages;
import com.github.imdmk.automessage.command.reload.messages.ENReloadMessages;
import com.github.imdmk.automessage.command.view.messages.ENViewMessages;
import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.platform.litecommands.messages.ENLiteCommandsMessages;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

@Header({
        "# ============================================================================",
        "#                          AutoMessage - messages.yml",
        "# ============================================================================",
        "# Everything the plugin itself says to a player: command replies, errors and",
        "# confirmations. The announcements you schedule live in scheduledMessages.yml.",
        "#",
        "# This file is English, and it is also the fallback: a player whose language",
        "# has no file of its own reads these messages.",
        "#",
        "# Shipped alongside it:",
        "#   messages_pl.yml    Polish",
        "#   messages_de.yml    German",
        "#",
        "# Each player sees the file matching the language their Minecraft client is",
        "# set to. Nothing to enable, and no per-player setting to store.",
        "#",
        "# To add a language, copy this file to messages_<code>.yml and translate it -",
        "# for example messages_fr.yml or messages_pt_br.yml.",
        "#",
        "# Formatting is MiniMessage: <red>, <bold>, <gradient:#ff0000:#00ff00>, ...",
        "# Reload with /automessage reload - no restart required.",
        "#",
        "# Source Code:",
        "#   https://github.com/imDMK/AutoMessage",
        "#",
        "# Support development:",
        "#   GitHub Sponsors: https://github.com/sponsors/imDMK",
        "#   PayPal:          https://paypal.me/dominiksuliga",
        "#",
        "# ============================================================================"
})
public class ENMessageConfig extends ConfigSection implements MessageConfig {

    @Comment({"#", "# Permission errors, usage hints and syntax messages.", "#"})
    public ENLiteCommandsMessages liteCommandsMessages = new ENLiteCommandsMessages();

    @Comment({"#", "# Replies to /automessage enable and /automessage disable.", "#"})
    public ENDispatcherMessages dispatcherMessages = new ENDispatcherMessages();

    @Comment({"#", "# Replies to /automessage reload.", "#"})
    public ENReloadMessages reloadMessages = new ENReloadMessages();

    @Comment({"#", "# Replies to /automessage view.", "#"})
    public ENViewMessages viewMessages = new ENViewMessages();

    @Override
    public ENLiteCommandsMessages liteCommandsMessages() {
        return liteCommandsMessages;
    }

    @Override
    public ENDispatcherMessages dispatcherMessages() {
        return dispatcherMessages;
    }

    @Override
    public ENReloadMessages reloadMessages() {
        return reloadMessages;
    }

    @Override
    public ENViewMessages viewMessages() {
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
        return "messages.yml";
    }
}
