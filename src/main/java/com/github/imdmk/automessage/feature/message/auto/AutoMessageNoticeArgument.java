package com.github.imdmk.automessage.feature.message.auto;

import com.github.imdmk.automessage.feature.message.MessageConfig;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AutoMessageNoticeArgument extends ArgumentResolver<CommandSender, AutoMessageNotice> {

    private final MessageConfig messageConfig;
    private final AutoMessageNoticeConfig noticeConfig;

    public AutoMessageNoticeArgument(@NotNull MessageConfig messageConfig, @NotNull AutoMessageNoticeConfig noticeConfig) {
        this.messageConfig = Objects.requireNonNull(messageConfig, "messageConfig cannot be null");
        this.noticeConfig = Objects.requireNonNull(noticeConfig, "noticeConfig cannot be null");
    }

    @Override
    protected ParseResult<AutoMessageNotice> parse(Invocation<CommandSender> invocation, Argument<AutoMessageNotice> context, String argument) {
        return this.noticeConfig.getMessage(argument)
                .map(ParseResult::success)
                .orElseGet(() -> ParseResult.failure(this.messageConfig.autoMessageNotFound));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<AutoMessageNotice> argument, SuggestionContext context) {
        return this.noticeConfig.messages.stream()
                .map(AutoMessageNotice::getName)
                .collect(SuggestionResult.collector());
    }
}
