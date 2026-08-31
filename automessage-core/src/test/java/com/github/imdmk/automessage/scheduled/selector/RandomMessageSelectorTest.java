package com.github.imdmk.automessage.scheduled.selector;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomMessageSelectorTest {

    private static ScheduledMessage msg(String name) {
        return new ScheduledMessage(name, List.of());
    }

    @Test
    @DisplayName("Random selector should return empty if list is empty")
    void shouldReturnEmptyOnEmptyList() {
        RandomMessageSelector selector = new RandomMessageSelector();

        assertTrue(selector.selectNext(List.of()).isEmpty());
    }

    @Test
    @DisplayName("Random selector should always return an element from the list")
    void shouldReturnElementFromList() {
        RandomMessageSelector selector = new RandomMessageSelector();
        List<ScheduledMessage> messages = List.of(msg("a"), msg("b"), msg("c"));

        ScheduledMessage result = selector.selectNext(messages).orElseThrow();

        assertTrue(messages.contains(result));
    }

    @Test
    @DisplayName("Random selector should return varying results across multiple calls")
    void shouldReturnDifferentElements() {
        RandomMessageSelector selector = new RandomMessageSelector();
        List<ScheduledMessage> messages = List.of(msg("a"), msg("b"), msg("c"));

        boolean variationFound = false;

        ScheduledMessage first = selector.selectNext(messages).orElseThrow();

        for (int i = 0; i < 50; i++) {
            if (!selector.selectNext(messages).orElseThrow().equals(first)) {
                variationFound = true;
                break;
            }
        }

        assertTrue(variationFound, "Random selector returned the same element in all iterations — suspicious randomness");
    }
}
