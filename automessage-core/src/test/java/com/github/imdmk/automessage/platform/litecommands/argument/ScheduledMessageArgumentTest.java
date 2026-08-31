package com.github.imdmk.automessage.platform.litecommands.argument;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class ScheduledMessageArgumentTest {

    private static final ScheduledMessage FIRST = message("first-message");
    private static final ScheduledMessage SECOND = message("second-message");

    @SuppressWarnings("unchecked")
    private final Invocation<CommandSender> invocation = mock(Invocation.class);

    @SuppressWarnings("unchecked")
    private final Argument<ScheduledMessage> argument = mock(Argument.class);

    private final ScheduledMessageArgument resolver = new ScheduledMessageArgument(new FakeRepository());

    private static ScheduledMessage message(String name) {
        return new ScheduledMessage(name, List.of());
    }

    @Test
    @DisplayName("parse(): should resolve a configured message by name")
    void parse_shouldResolveConfiguredMessage() {
        AtomicReference<ScheduledMessage> parsed = new AtomicReference<>();

        resolver.parse(invocation, argument, "first-message").whenSuccessful(parsed::set);

        assertEquals(FIRST, parsed.get());
    }

    @Test
    @DisplayName("parse(): should fail with the typed name when no message matches")
    void parse_shouldFailForUnknownMessage() {
        AtomicReference<Object> reason = new AtomicReference<>();

        resolver.parse(invocation, argument, "nope").whenFailed(failure -> reason.set(failure.getReason()));

        assertNotNull(reason.get());
        assertInstanceOf(UnknownScheduledMessage.class, reason.get());
        assertEquals("nope", ((UnknownScheduledMessage) reason.get()).name());
    }

    @Test
    @DisplayName("suggest(): should suggest every configured message name")
    void suggest_shouldSuggestConfiguredNames() {
        List<String> suggestions = resolver
                .suggest(invocation, argument, new SuggestionContext(""))
                .asMultiLevelList();

        assertEquals(List.of("first-message", "second-message"), suggestions);
    }

    private static final class FakeRepository implements ScheduledMessageRepository {

        @Override
        public List<ScheduledMessage> findAll() {
            return List.of(FIRST, SECOND);
        }

        @Override
        public Optional<ScheduledMessage> findByName(String name) {
            return findAll().stream()
                    .filter(message -> message.name().equalsIgnoreCase(name))
                    .findFirst();
        }

        @Override
        public List<ScheduledMessage> findScheduled() {
            return findAll().stream().filter(ScheduledMessage::isScheduled).toList();
        }

        @Override
        public List<ScheduledMessage> findByChannel(AnnouncementChannel channel) {
            return findScheduled().stream()
                    .filter(message -> message.belongsTo(channel))
                    .toList();
        }

        @Override
        public List<ScheduledMessage> findByTrigger(MessageTrigger.Type type) {
            return findAll().stream()
                    .filter(message -> message.trigger() != null && message.trigger().type() == type)
                    .toList();
        }

        @Override
        public List<String> names() {
            return List.of(FIRST.name(), SECOND.name());
        }
    }
}
