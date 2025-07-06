package com.github.imdmk.automessage.feature.command.implementation;

import com.github.imdmk.automessage.feature.message.MessageService;
import com.github.imdmk.automessage.feature.message.auto.AutoMessageNotice;
import com.github.imdmk.automessage.feature.message.auto.dispatcher.AutoMessageDispatcher;
import com.github.imdmk.automessage.feature.message.auto.eligibility.AutoMessageEligibilityEvaluator;
import com.github.imdmk.automessage.feature.message.auto.selector.AutoMessageSelector;
import com.github.imdmk.automessage.feature.message.auto.selector.AutoMessageSelectorFactory;
import com.github.imdmk.automessage.feature.message.auto.selector.AutoMessageSelectorMode;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Command(name = "automessage dispatch")
@Permission("command.automessage.dispatch")
public class DispatchCommand {

    private final Server server;
    private final MessageService messageService;
    private final AutoMessageDispatcher dispatcher;
    private final AutoMessageEligibilityEvaluator evaluator;
    private final AutoMessageSelector randomSelector;

    public DispatchCommand(
            @NotNull Server server,
            @NotNull MessageService messageService,
            @NotNull AutoMessageDispatcher dispatcher,
            @NotNull AutoMessageEligibilityEvaluator evaluator
    ) {
        this.server = Objects.requireNonNull(server, "server cannot be null");
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher cannot be null");
        this.evaluator = Objects.requireNonNull(evaluator, "evaluator cannot be null");
        this.randomSelector = AutoMessageSelectorFactory.create(AutoMessageSelectorMode.RANDOM, evaluator);
    }

    @Execute(name = "random")
    void random(@Context CommandSender sender) {
        this.server.getOnlinePlayers().forEach(player -> this.dispatcher.dispatch(player, this.randomSelector));
        this.messageService.send(sender, notice -> notice.autoMessageRandomDispatched);
    }

    @Execute(name = "random")
    void random(@Context CommandSender sender, @Arg Player player) {
        this.dispatcher.dispatch(player, this.randomSelector);
        this.messageService.send(sender, notice -> notice.autoMessageRandomDispatched);
    }

    @Execute(name = "select")
    void select(@Context CommandSender sender, @Arg AutoMessageNotice autoMessage) {
        this.server.getOnlinePlayers()
                .stream()
                .filter(player -> this.evaluator.canReceive(player, autoMessage))
                .forEach(player -> this.dispatcher.dispatch(player, autoMessage));

        this.messageService.send(sender, notice -> notice.autoMessageSelectedDispatched);
    }

    @Execute(name = "select")
    void select(@Context CommandSender sender, @Arg AutoMessageNotice autoMessage, @Arg Player player) {
        if (!this.evaluator.canReceive(player, autoMessage)) {
            this.messageService.send(sender, notice -> notice.autoMessageSelectedCannotReceive);
            return;
        }

        this.dispatcher.dispatch(player, autoMessage);
        this.messageService.send(sender, notice -> notice.autoMessageSelectedDispatched);
    }
}
