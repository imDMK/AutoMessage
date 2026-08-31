package com.github.imdmk.automessage.scheduled.channel;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageBuilder;
import com.github.imdmk.automessage.config.ConfigReloadService;
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
        assertThat(new ScheduledMessage("m", List.of()).channel())
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

        ScheduledMessageRepository repository = ScheduledMessageRepository.config(config, new ConfigReloadService(null));

        assertThat(repository.findByChannel(channel("default")))
                .extracting(ScheduledMessage::name).containsExactly("tip");
        assertThat(repository.findByChannel(channel("ads")))
                .extracting(ScheduledMessage::name).containsExactly("advert", "another-advert");
        assertThat(repository.findByChannel(channel("nobody"))).isEmpty();
    }

    @Test
    @DisplayName("every channel is spelled out in the list, with none implied")
    void channelsAreExplicit() {
        MessageDispatcherConfig config = new MessageDispatcherConfig();
        config.channels = List.of(channel("default"), channel("ads"));

        assertThat(config.channels())
                .extracting(AnnouncementChannel::name)
                .containsExactly("default", "ads");
    }

    @Test
    @DisplayName("a server may drop the default channel entirely and run only its own")
    void defaultChannelIsNotForced() {
        MessageDispatcherConfig config = new MessageDispatcherConfig();
        config.channels = List.of(channel("ads"));

        // Nothing re-adds 'default': what the file says is what runs, which is the point of
        // making the list the single source.
        assertThat(config.channels()).extracting(AnnouncementChannel::name).containsExactly("ads");
    }
}
