package com.github.imdmk.automessage.scheduled.trigger;

import com.github.imdmk.automessage.platform.time.DurationFormatter;
import com.github.imdmk.automessage.platform.time.DurationParser;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class MessageTriggerSerializer implements ObjectSerializer<MessageTrigger> {

    @Override
    public boolean supports(@NotNull Class<? super MessageTrigger> type) {
        return MessageTrigger.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(MessageTrigger trigger, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("type", trigger.type(), MessageTrigger.Type.class);

        switch (trigger) {
            case JoinTrigger join -> {
                if (!join.delay().isZero()) {
                    data.add("delay", DurationFormatter.format(join.delay()), String.class);
                }
            }
            case PlayerCountTrigger count -> data.add("threshold", count.threshold(), Integer.class);
        }
    }

    @Override
    public MessageTrigger deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        final MessageTrigger.Type type = data.get("type", MessageTrigger.Type.class);

        final Duration delay = data.containsKey("delay")
                ? DurationParser.parse(data.get("delay", String.class))
                : Duration.ZERO;

        return switch (type) {
            case JOIN -> new JoinTrigger(delay, false);
            case FIRST_JOIN -> new JoinTrigger(delay, true);
            case PLAYER_COUNT -> new PlayerCountTrigger(data.get("threshold", Integer.class));
        };
    }
}
