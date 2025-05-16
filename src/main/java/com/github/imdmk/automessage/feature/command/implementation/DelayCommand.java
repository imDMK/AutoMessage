package com.github.imdmk.automessage.feature.command.implementation;

import com.github.imdmk.automessage.feature.message.MessageService;
import com.github.imdmk.automessage.feature.message.auto.dispatcher.AutoMessageDispatcher;
import com.github.imdmk.automessage.util.DurationUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;

@Command(name = "automessage delay")
public class DelayCommand {

    private final MessageService messageService;
    private final AutoMessageDispatcher autoMessageDispatcher;

    public DelayCommand(
            @NotNull MessageService messageService,
            @NotNull AutoMessageDispatcher autoMessageDispatcher
    ) {
        this.messageService = Objects.requireNonNull(Objects.requireNonNull(messageService, "messageService cannot be null"));
        this.autoMessageDispatcher = Objects.requireNonNull(autoMessageDispatcher, "autoMessageDispatcher cannot be null");
    }

    @Execute
    void show(@Context CommandSender sender) {
        this.messageService.create()
                .viewer(sender)
                .notice(notice -> notice.autoMessageDelay)
                .placeholder("{DELAY}", DurationUtil.format(this.autoMessageDispatcher.getDelay()))
                .send();
    }

    @Execute(name = "set")
    void set(@Context CommandSender sender, @Arg Duration delay) {
        this.autoMessageDispatcher.changeDelay(DurationUtil.toTicks(delay));

        this.messageService.create()
                .viewer(sender)
                .notice(notice -> notice.autoMessageDelayChange)
                .placeholder("{DELAY}", DurationUtil.format(delay))
                .send();
    }
}
