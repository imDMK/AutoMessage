package com.github.imdmk.automessage.message;

import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.command.dispatcher.messages.ENDispatcherMessages;
import com.github.imdmk.automessage.command.reload.messages.ENReloadMessages;
import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.platform.litecommands.messages.ENLiteCommandsMessages;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

@Header({
        "# ============================================================================",
        "#                              AutoMessage — messages.yml",
        "# ============================================================================",
        "# This configuration file defines all user-facing messages used by AutoMessage.",
        "# It centralizes command responses, dispatcher status messages, and reload",
        "# notifications, allowing full customization without modifying code.",
        "#",
        "# Sections:",
        "#  • liteCommandsMessages   — errors, usage prompts, and permission responses",
        "#  • dispatcherMessages     — messages for enabling/disabling automatic dispatching",
        "#  • reloadMessages         — messages shown during configuration reload operations",
        "#",
        "# Editing Tips:",
        "#  • Colors follow MiniMessage syntax (<red>, <yellow>, <gray>, <rainbow>, etc.).",
        "#  • Reload the plugin after editing: /automessage reload",
        "#",
        "# Source Code:",
        "#   https://github.com/imDMK/AutoMessage",
        "#",
        "# Support development:",
        "#   GitHub Sponsors: https://github.com/sponsors/imDMK",
        "#   PayPal:          https://paypal.me/dominiksuliga",
        "#",
        "# Thank you for using AutoMessage!",
        "# ============================================================================"
})
public final class MessageConfig extends ConfigSection {

    @Comment({
            "#",
            "# Messages used by the LiteCommands subsystem.",
            "# Contains permission errors, usage hints, and syntax messages.",
            "#"
    })
    public ENLiteCommandsMessages liteCommandsMessages = new ENLiteCommandsMessages();

    @Comment({
            "#",
            "# Messages used by dispatcher-related commands.",
            "#"
    })
    public ENDispatcherMessages dispatcherMessages = new ENDispatcherMessages();

    @Comment({
            "#",
            "# Messages used during configuration reload commands.",
            "# Includes success and failure notifications.",
            "#"
    })
    public ENReloadMessages reloadMessages = new ENReloadMessages();

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new MultificationSerdesPack(NoticeResolverDefaults.createRegistry()));
        };
    }

    @Override
    public String getFileName() {
        return "messages.yml";
    }
}
