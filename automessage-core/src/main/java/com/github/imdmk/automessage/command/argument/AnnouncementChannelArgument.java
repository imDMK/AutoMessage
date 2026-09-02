package com.github.imdmk.automessage.command.argument;

import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;

public final class AnnouncementChannelArgument<S> extends ArgumentResolver<S, AnnouncementChannel> {

    private final MessageDispatcherConfig dispatcherConfig;

    public AnnouncementChannelArgument(MessageDispatcherConfig dispatcherConfig) {
        this.dispatcherConfig = dispatcherConfig;
    }

    // Read from the configuration on every invocation rather than captured once, so a channel
    // added by a reload can be named without restarting the server.
    @Override
    protected ParseResult<AnnouncementChannel> parse(
            Invocation<S> invocation,
            Argument<AnnouncementChannel> context,
            String argument
    ) {
        return dispatcherConfig.channels().stream()
                .filter(channel -> channel.matches(argument))
                .findFirst()
                .<ParseResult<AnnouncementChannel>>map(ParseResult::success)
                .orElseGet(() -> ParseResult.failure(new UnknownAnnouncementChannel(argument)));
    }

    @Override
    public SuggestionResult suggest(
            Invocation<S> invocation,
            Argument<AnnouncementChannel> argument,
            SuggestionContext context
    ) {
        return SuggestionResult.of(dispatcherConfig.channels().stream().map(AnnouncementChannel::name).toList());
    }
}
