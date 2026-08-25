package com.github.imdmk.automessage.platform.litecommands.argument;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

/**
 * Resolves a {@link ScheduledMessage} from its configured name and suggests the names currently
 * present in {@code scheduledMessages.yml}.
 *
 * <p>
 * Suggestions are read on every keystroke, so messages added by an administrator become
 * available right after {@code /automessage reload}.
 * </p>
 */
public final class ScheduledMessageArgument extends ArgumentResolver<CommandSender, ScheduledMessage> {

    private final ScheduledMessageRepository repository;

    public ScheduledMessageArgument(ScheduledMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    protected ParseResult<ScheduledMessage> parse(
            Invocation<CommandSender> invocation,
            Argument<ScheduledMessage> context,
            String argument
    ) {
        return repository.findByName(argument)
                .<ParseResult<ScheduledMessage>>map(ParseResult::success)
                .orElseGet(() -> ParseResult.failure(new UnknownScheduledMessage(argument)));
    }

    @Override
    public SuggestionResult suggest(
            Invocation<CommandSender> invocation,
            Argument<ScheduledMessage> argument,
            SuggestionContext context
    ) {
        return SuggestionResult.of(repository.names());
    }
}
