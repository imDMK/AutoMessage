package com.github.imdmk.automessage.command.view;

import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageSender;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Shows a single scheduled message to the player who ran the command, exactly as the dispatcher
 * would render it.
 *
 * <p>
 * The preview deliberately ignores the audience rules of the message, so staff can check how a
 * message restricted to a permission or a group looks without holding that permission.
 * </p>
 */
@Command(name = "automessage view")
@Permission("command.automessage.view")
public final class ViewCommand {

    private final ScheduledMessageSender sender;
    private final MessageService messageService;

    public ViewCommand(
            ScheduledMessageSender sender,
            MessageService messageService
    ) {
        this.sender = sender;
        this.messageService = messageService;
    }

    @Execute
    void view(
            @Context CommandSender commandSender,
            @Arg("message") ScheduledMessage message
    ) {
        if (!(commandSender instanceof Player player)) {
            messageService.send(commandSender, n -> n.commands.viewPlayerOnly);
            return;
        }

        sender.send(player, message);

        messageService.create()
                .viewer(player)
                .notice(notice -> notice.commands.messagePreviewed)
                .placeholder("{MESSAGE}", message.name())
                .send();
    }
}
