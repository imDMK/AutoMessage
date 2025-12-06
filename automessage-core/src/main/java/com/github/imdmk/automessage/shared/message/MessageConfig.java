package com.github.imdmk.automessage.shared.message;

import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.command.dispatcher.messages.DispatcherMessagesImpl;
import com.github.imdmk.automessage.command.reload.messages.ReloadMessagesImpl;
import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.platform.litecommands.messages.LiteCommandsMessagesImpl;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

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
