package com.github.imdmk.automessage.scheduled.channel;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageBuilder;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnnouncementChannelTest {

    private static ScheduledMessage message(String name, String channel) {
        return ScheduledMessageBuilder.create()
                .name(name)
                .addNotice(Notice.chat("x"))
                .channel(channel)
                .build();
    }

    private static AnnouncementChannel channel(String name) {
        return new AnnouncementChannel(
                name, true, Duration.ZERO, Duration.ofSeconds(30), MessageSelectorType.SEQUENTIAL
        );
    }

    @Test
    @DisplayName("a message that names no channel joins the default one")
    void missingChannelBecomesDefault() {
        assertThat(message("m", null).channel()).isEqualTo(AnnouncementChannel.DEFAULT_NAME);
        assertThat(new ScheduledMessage("m", List.of(Notice.chat("x")), List.of()).channel())
                .isEqualTo(AnnouncementChannel.DEFAULT_NAME);
    }

    @Test
    @DisplayName("channel names are matched ignoring case and surrounding space")
    void channelNamesAreNormalised() {
        assertThat(message("m", "  Ads ").belongsTo(channel("ads"))).isTrue();
        assertThat(message("m", "ads").belongsTo(channel("ADS"))).isTrue();
        assertThat(message("m", "ads").belongsTo(channel("tips"))).isFalse();
    }

    @Test
    @DisplayName("the repository hands each channel only its own messages")
    void repositorySplitsByChannel() {
        ScheduledMessagesConfig config = new ScheduledMessagesConfig();
        config.messages = List.of(
                message("tip", "default"),
                message("advert", "ads"),
                message("another-advert", "ads")
        );

        ScheduledMessageRepository repository = ScheduledMessageRepository.config(config);

        assertThat(repository.findByChannel(channel("default")))
                .extracting(ScheduledMessage::name).containsExactly("tip");
        assertThat(repository.findByChannel(channel("ads")))
                .extracting(ScheduledMessage::name).containsExactly("advert", "another-advert");
        assertThat(repository.findByChannel(channel("nobody"))).isEmpty();
    }

    @Test
    @DisplayName("the legacy top-level timing keeps working as the default channel")
    void legacyConfigBecomesTheDefaultChannel() {
        MessageDispatcherConfig config = new MessageDispatcherConfig();
        config.initialDelay = Duration.ofSeconds(5);
        config.period = Duration.ofMinutes(2);
        config.selector = MessageSelectorType.RANDOM;

        List<AnnouncementChannel> channels = config.channels();

        assertThat(channels).hasSize(1);
        assertThat(channels.getFirst().isDefault()).isTrue();
        assertThat(channels.getFirst().period()).isEqualTo(Duration.ofMinutes(2));
        assertThat(channels.getFirst().selector()).isEqualTo(MessageSelectorType.RANDOM);
    }

    @Test
    @DisplayName("extra channels run alongside the default one")
    void extraChannelsAreAppended() {
        MessageDispatcherConfig config = new MessageDispatcherConfig();
        config.channels = List.of(new AnnouncementChannel(
                "ads", true, Duration.ofMinutes(1), Duration.ofMinutes(15), MessageSelectorType.RANDOM
        ));

        assertThat(config.channels())
                .extracting(AnnouncementChannel::name)
                .containsExactly(AnnouncementChannel.DEFAULT_NAME, "ads");
    }

    @Test
    @DisplayName("a channel redeclaring 'default' does not schedule it twice")
    void redeclaredDefaultIsIgnored() {
        MessageDispatcherConfig config = new MessageDispatcherConfig();
        config.channels = List.of(channel("Default"));

        // Scheduling it twice would send every default-channel message in duplicate.
        assertThat(config.channels()).hasSize(1);
    }
}
