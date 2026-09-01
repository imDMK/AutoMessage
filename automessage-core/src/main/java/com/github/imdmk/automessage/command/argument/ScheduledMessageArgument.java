package com.github.imdmk.automessage.command.argument;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;

public final class ScheduledMessageArgument<S> extends ArgumentResolver<S, ScheduledMessage> {

    private final ScheduledMessageRepository repository;

    public ScheduledMessageArgument(ScheduledMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    protected ParseResult<ScheduledMessage> parse(
            Invocation<S> invocation,
            Argument<ScheduledMessage> context,
            String argument
    ) {
        return repository.findByName(argument)
                .<ParseResult<ScheduledMessage>>map(ParseResult::success)
                .orElseGet(() -> ParseResult.failure(new UnknownScheduledMessage(argument)));
    }

    @Override
    public SuggestionResult suggest(
            Invocation<S> invocation,
            Argument<ScheduledMessage> argument,
            SuggestionContext context
    ) {
        return SuggestionResult.of(repository.names());
    }
}
