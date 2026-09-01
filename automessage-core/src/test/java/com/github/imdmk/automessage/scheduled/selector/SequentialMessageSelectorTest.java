package com.github.imdmk.automessage.scheduled.selector;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SequentialMessageSelectorTest {

    private static ScheduledMessage msg(String name) {
        return new ScheduledMessage(name, List.of());
    }

    @Test
    @DisplayName("Sequential selector should return empty if list is empty")
    void shouldReturnEmptyOnEmptyList() {
        SequentialMessageSelector selector = new SequentialMessageSelector();

        assertTrue(selector.selectNext(List.of(), true).isEmpty());
    }

@Test
    @DisplayName("Sequential selector should not advance index when advanceIndex=false")
    void shouldNotAdvanceIndexWhenFlagFalse() {
        SequentialMessageSelector selector = new SequentialMessageSelector();
        List<ScheduledMessage> messages = List.of(msg("a"), msg("b"));

        assertEquals("a", selector.selectNext(messages, false).get().name());
        assertEquals("a", selector.selectNext(messages, false).get().name());
        assertEquals("a", selector.selectNext(messages, false).get().name());
    }

    @Test
    @DisplayName("Sequential selector should advance index when advanceIndex=true")
    void shouldAdvanceIndexWhenTrue() {
        SequentialMessageSelector selector = new SequentialMessageSelector();
        List<ScheduledMessage> messages = List.of(msg("a"), msg("b"));

        assertEquals("a", selector.selectNext(messages, true).get().name());
        assertEquals("b", selector.selectNext(messages, true).get().name());
        assertEquals("a", selector.selectNext(messages, true).get().name()); // wraps
    }

}

