package com.github.imdmk.automessage.scheduled.trigger;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageBuilder;
import com.github.imdmk.automessage.config.ConfigReloadService;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageTriggerTest {

    private static ScheduledMessage message(String name, MessageTrigger trigger) {
        return ScheduledMessageBuilder.create()
                .name(name)
                .trigger(trigger)
                .build();
    }

    @Test
    @DisplayName("a triggered message is kept out of the timed rotation")
    void triggeredMessagesLeaveTheRotation() {
        ScheduledMessagesConfig config = new ScheduledMessagesConfig();
        config.messages = List.of(
                message("rotating", null),
                message("greeting", MessageTrigger.join(Duration.ZERO)),
                message("milestone", MessageTrigger.playerCount(100))
        );

        ScheduledMessageRepository repository = ScheduledMessageRepository.config(config, new ConfigReloadService(null));

        assertThat(repository.findScheduled())
                .extracting(ScheduledMessage::name).containsExactly("rotating");
        assertThat(repository.findByTrigger(MessageTrigger.Type.JOIN))
                .extracting(ScheduledMessage::name).containsExactly("greeting");
        assertThat(repository.findByTrigger(MessageTrigger.Type.PLAYER_COUNT))
                .extracting(ScheduledMessage::name).containsExactly("milestone");
    }

    @Test
    @DisplayName("FIRST_JOIN only applies to a player the server has never seen")
    void firstJoinExcludesReturningPlayers() {
        JoinTrigger firstJoin = MessageTrigger.firstJoin(Duration.ZERO);

        assertThat(firstJoin.appliesTo(true)).isTrue();
        assertThat(firstJoin.appliesTo(false)).isFalse();
        assertThat(firstJoin.type()).isEqualTo(MessageTrigger.Type.FIRST_JOIN);
    }

    @Test
    @DisplayName("a plain JOIN trigger applies to everyone")
    void plainJoinAppliesToEveryone() {
        JoinTrigger join = MessageTrigger.join(Duration.ofSeconds(3));

        assertThat(join.appliesTo(false)).isTrue();
        assertThat(join.appliesTo(true)).isTrue();
        assertThat(join.type()).isEqualTo(MessageTrigger.Type.JOIN);
    }

    @Test
    @DisplayName("a negative or missing delay is treated as no delay")
    void delayIsNormalised() {
        assertThat(new JoinTrigger(null, false).delay()).isEqualTo(Duration.ZERO);
        assertThat(new JoinTrigger(Duration.ofSeconds(-5), false).delay()).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("a milestone fires once and rearms only after the count drops back")
    void milestonesFireOncePerCrossing() {
        PlayerCountMilestones milestones = new PlayerCountMilestones();

        assertThat(milestones.reach(100, 99)).isFalse();
        assertThat(milestones.reach(100, 100)).isTrue();

        // Hovering on the boundary must not re-announce the same milestone.
        assertThat(milestones.reach(100, 101)).isFalse();
        assertThat(milestones.reach(100, 100)).isFalse();

        assertThat(milestones.reach(100, 80)).isFalse();
        assertThat(milestones.reach(100, 100)).isTrue();
    }

    @Test
    @DisplayName("thresholds are tracked independently of one another")
    void milestonesAreIndependent() {
        PlayerCountMilestones milestones = new PlayerCountMilestones();

        assertThat(milestones.reach(50, 60)).isTrue();
        assertThat(milestones.reach(100, 60)).isFalse();
        assertThat(milestones.reach(100, 100)).isTrue();
        assertThat(milestones.reach(50, 100)).isFalse();
    }

    @Test
    @DisplayName("a threshold below one is a configuration error")
    void rejectsMeaninglessThreshold() {
        assertThatThrownBy(() -> MessageTrigger.playerCount(0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
