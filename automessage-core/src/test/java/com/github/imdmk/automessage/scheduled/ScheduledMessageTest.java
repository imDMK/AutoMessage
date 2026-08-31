package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScheduledMessageTest {

    @Test
    @DisplayName("carries only what decides when a message is sent and to whom")
    void holdsScheduling() {
        AudienceRule rule = AudienceRule.permission("test.permission");

        ScheduledMessage message = new ScheduledMessage("example", List.of(rule));

        assertThat(message.name()).isEqualTo("example");
        assertThat(message.rules()).containsExactly(rule);
        assertThat(message.weight()).isEqualTo(ScheduledMessage.DEFAULT_WEIGHT);
        assertThat(message.channel()).isEqualTo(AnnouncementChannel.DEFAULT_NAME);
        assertThat(message.trigger()).isNull();
        assertThat(message.isScheduled()).isTrue();
    }

    @Test
    @DisplayName("a message with no rules is not a configuration error - it goes to everyone")
    void rulesAreOptional() {
        assertThat(new ScheduledMessage("example", List.of()).rules()).isEmpty();
    }

    @Test
    @DisplayName("rejects a message with no name, since the name is what ties it to its text")
    void requiresAName() {
        assertThatThrownBy(() -> new ScheduledMessage(null, List.of()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("rejects a negative weight")
    void rejectsNegativeWeight() {
        assertThatThrownBy(() ->
                new ScheduledMessage("m", List.of(), -1, AnnouncementChannel.DEFAULT_NAME, null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("hands out an unmodifiable rule list")
    void rulesAreUnmodifiable() {
        ScheduledMessage message = new ScheduledMessage("m", List.of(AudienceRule.permission("p")));

        assertThatThrownBy(() -> message.rules().add(AudienceRule.group("vip")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("a triggered message leaves the timed rotation")
    void triggeredMessagesAreNotScheduled() {
        ScheduledMessage message = ScheduledMessageBuilder.create()
                .name("welcome")
                .trigger(MessageTrigger.firstJoin(Duration.ofSeconds(3)))
                .build();

        assertThat(message.isScheduled()).isFalse();
    }
}
