package com.github.imdmk.automessage.scheduled.selector;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShuffleMessageSelectorTest {

    private final MessageSelector selector = MessageSelectorFactory.create(MessageSelectorType.SHUFFLE);

    private static ScheduledMessage message(String name) {
        return new ScheduledMessage(name, List.of(Notice.chat("x")), List.of());
    }

    private static List<ScheduledMessage> messages(String... names) {
        List<ScheduledMessage> messages = new ArrayList<>();
        for (String name : names) {
            messages.add(message(name));
        }
        return List.copyOf(messages);
    }

    private List<String> drain(List<ScheduledMessage> source, int count) {
        List<String> drawn = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            drawn.add(selector.selectNext(source).orElseThrow().name());
        }
        return drawn;
    }

    @Test
    @DisplayName("shows every message once before repeating any")
    void dealsAWholeDeckBeforeReshuffling() {
        List<ScheduledMessage> source = messages("a", "b", "c", "d");

        assertThat(drain(source, 4)).containsExactlyInAnyOrder("a", "b", "c", "d");
    }

    @Test
    @DisplayName("reshuffles once the deck runs out")
    void reshufflesAfterExhaustion() {
        List<ScheduledMessage> source = messages("a", "b", "c");

        List<String> twoRounds = drain(source, 6);

        assertThat(twoRounds.subList(0, 3)).containsExactlyInAnyOrder("a", "b", "c");
        assertThat(twoRounds.subList(3, 6)).containsExactlyInAnyOrder("a", "b", "c");
    }

    @Test
    @DisplayName("a preview does not consume the message it previews")
    void previewLeavesTheDeckAlone() {
        List<ScheduledMessage> source = messages("a", "b", "c");

        String previewed = selector.selectNext(source, false).orElseThrow().name();

        assertThat(selector.selectNext(source, true).orElseThrow().name()).isEqualTo(previewed);
    }

    @Test
    @DisplayName("re-deals when a reload changes the message list")
    void reDealsWhenTheSourceChanges() {
        selector.selectNext(messages("a", "b", "c"));

        // "c" is gone after the reload and must never be announced again.
        List<ScheduledMessage> reloaded = messages("a", "b");

        assertThat(drain(reloaded, 2)).containsExactlyInAnyOrder("a", "b");
    }

    @Test
    @DisplayName("returns nothing when there are no messages")
    void emptyStaysEmpty() {
        assertThat(selector.selectNext(List.of())).isEmpty();
    }
}
