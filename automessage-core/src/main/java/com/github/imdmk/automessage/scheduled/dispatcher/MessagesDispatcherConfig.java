package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class MessagesDispatcherConfig extends ConfigSection {

    @Comment("# Should the automatic dispatcher enabled?")
    public boolean enabled = true;

    @Comment("# Delay between messages (time between dispatch executions).")
    public Duration period = Duration.ofSeconds(10);

    @Comment("# How long to wait before the first automatic dispatch.")
    public Duration initialDelay = Duration.ofSeconds(10);

    @Comment("# Strategy used to select the next message.")
    public MessageSelectorType selector = MessageSelectorType.SEQUENTIAL;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public @NotNull String getFileName() {
        return "messagesDispatcher.yml";
    }
}
