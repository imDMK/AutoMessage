package com.github.imdmk.automessage.scheduled.channel;

import com.github.imdmk.automessage.platform.time.DurationFormatter;
import com.github.imdmk.automessage.platform.time.DurationParser;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class AnnouncementChannelSerializer implements ObjectSerializer<AnnouncementChannel> {

    @Override
    public boolean supports(@NotNull Class<? super AnnouncementChannel> type) {
        return AnnouncementChannel.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(
            AnnouncementChannel channel,
            SerializationData data,
            @NotNull GenericsDeclaration generics
    ) {
        data.add("name", channel.name(), String.class);
        data.add("enabled", channel.enabled(), Boolean.class);
        data.add("initialDelay", DurationFormatter.format(channel.initialDelay()), String.class);
        data.add("period", DurationFormatter.format(channel.period()), String.class);
        data.add("selector", channel.selector(), MessageSelectorType.class);
    }

    @Override
    public AnnouncementChannel deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        return new AnnouncementChannel(
                data.get("name", String.class),
                !data.containsKey("enabled") || data.get("enabled", Boolean.class),
                data.containsKey("initialDelay")
                        ? DurationParser.parse(data.get("initialDelay", String.class))
                        : Duration.ZERO,
                data.containsKey("period")
                        ? DurationParser.parse(data.get("period", String.class))
                        : Duration.ofSeconds(30L),
                data.containsKey("selector")
                        ? data.get("selector", MessageSelectorType.class)
                        : MessageSelectorType.SEQUENTIAL
        );
    }
}
