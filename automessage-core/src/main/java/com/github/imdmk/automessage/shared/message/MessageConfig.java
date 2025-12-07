package com.github.imdmk.automessage.shared.message;

import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.command.dispatcher.messages.DispatcherMessagesImpl;
import com.github.imdmk.automessage.command.reload.messages.ReloadMessagesImpl;
import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.platform.litecommands.messages.LiteCommandsMessagesImpl;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

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
    public LiteCommandsMessagesImpl liteCommandsMessages = new LiteCommandsMessagesImpl();

    @Comment({
            "#",
            "# Messages used by dispatcher-related commands.",
            "#"
    })
    public DispatcherMessagesImpl dispatcherMessages = new DispatcherMessagesImpl();

    @Comment({
            "#",
            "# Messages used during configuration reload commands.",
            "# Includes success and failure notifications.",
            "#"
    })
    public ReloadMessagesImpl reloadMessages = new ReloadMessagesImpl();

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new MultificationSerdesPack(NoticeResolverDefaults.createRegistry()));
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "messages.yml";
    }
}
