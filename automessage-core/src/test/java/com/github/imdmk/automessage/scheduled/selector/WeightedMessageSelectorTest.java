package com.github.imdmk.automessage.scheduled.selector;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WeightedMessageSelectorTest {

    private final MessageSelector selector = MessageSelectorFactory.create(MessageSelectorType.WEIGHTED);

    private static ScheduledMessage message(String name, int weight) {
        return new ScheduledMessage(name, List.of(Notice.chat("x")), List.of(), weight);
    }

    private Map<String, Integer> draw(List<ScheduledMessage> source, int rounds) {
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < rounds; i++) {
            counts.merge(selector.selectNext(source).orElseThrow().name(), 1, Integer::sum);
        }
        return counts;
    }

    @Test
    @DisplayName("a heavier message is drawn far more often than a lighter one")
    void favoursTheHeavierMessage() {
        List<ScheduledMessage> source = List.of(message("heavy", 9), message("light", 1));

        Map<String, Integer> counts = draw(source, 10_000);

        // Expectation is 9000/1000; the bounds are wide enough that a fair implementation
        // essentially never trips them, while a uniform one always does.
        assertThat(counts.get("heavy")).isBetween(8_400, 9_600);
        assertThat(counts.get("light")).isBetween(400, 1_600);
    }

    @Test
    @DisplayName("weight 0 parks a message without deleting it")
    void zeroWeightIsNeverDrawn() {
        List<ScheduledMessage> source = List.of(message("parked", 0), message("active", 1));

        assertThat(draw(source, 500)).containsOnlyKeys("active");
    }

    @Test
    @DisplayName("returns nothing when every message is parked")
    void allParkedSelectsNothing() {
        List<ScheduledMessage> source = List.of(message("a", 0), message("b", 0));

        assertThat(selector.selectNext(source)).isEmpty();
    }

    @Test
    @DisplayName("behaves like a uniform draw while the weights are untouched")
    void defaultWeightsAreUniform() {
        List<ScheduledMessage> source = List.of(
                message("a", ScheduledMessage.DEFAULT_WEIGHT),
                message("b", ScheduledMessage.DEFAULT_WEIGHT)
        );

        Map<String, Integer> counts = draw(source, 10_000);

        assertThat(counts.get("a")).isBetween(4_500, 5_500);
        assertThat(counts.get("b")).isBetween(4_500, 5_500);
    }

    @Test
    @DisplayName("returns nothing when there are no messages")
    void emptyStaysEmpty() {
        assertThat(selector.selectNext(List.of())).isEmpty();
    }
}
